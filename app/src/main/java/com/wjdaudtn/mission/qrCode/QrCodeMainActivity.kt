package com.wjdaudtn.mission.qrCode

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.wjdaudtn.mission.databinding.ActivityQrCodeMainBinding
import com.wjdaudtn.mission.databinding.QrcodeBottomDialogBinding
import com.wjdaudtn.mission.todo.TodoMainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrCodeMainActivity : AppCompatActivity(), BottomDialog.FinishListener {
    override fun finish() {
        super.finish()
    }

    private lateinit var binding: ActivityQrCodeMainBinding
    private lateinit var previewView: PreviewView // 카메라 미리보기 뷰
    private lateinit var cameraExecutor: ExecutorService // 카메라 작업을 위한 ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider> // CameraX 라이브러리의 ProcessCameraProvider를 위한 Future 객체
    private lateinit var analysis: MyImageAnalyzer  // 이미지 분석기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permission()
        camera()

        val requestGalleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    Log.d("requestGalleryLauncher", "이미지 불러오기 성공")
                    val imageUri = it.data?.data // 선택한 이미지의 URI 가져오기

                    if (imageUri != null) {
                        val imageBitmap = getBitmapFromUri(imageUri) // URI를 사용하여 비트맵 이미지 가져오기
                        if (imageBitmap != null) {
                            analysis.analyzeBitmap(imageBitmap) // 비트맵 이미지를 분석기로 전달
                        }
                    }
                } else {
                    Log.d("requestGalleryLauncher", "실패")
                }
            }
        binding.btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            requestGalleryLauncher.launch(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun initView() {
        //툴바
        setSupportActionBar(binding.tbQrMain)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "QR Code"
        }

        onBackPressedDispatcher.addCallback(QrCodeMainActivity(),
            object : OnBackPressedCallback(true) { //뒤로가기 버튼
                override fun handleOnBackPressed() {
                    finish()
                }
            })
    }

    private fun camera() {
        previewView = binding.previewView  // 미리보기 뷰를 바인딩에서 가져옴
        window.setFlags(
            1024, 2014
        ) // 전체 화면 설정 (상태 바와 네비게이션 바 숨김) 1024 (WindowManager.LayoutParams.FLAG_FULLSCREEN): 상태 바(Status Bar)를 숨깁니다.  2014 (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN): 상태 바를 숨기고 화면이 꺼지지 않도록 유지합니다.
        cameraExecutor =
            Executors.newSingleThreadExecutor() // 단일 스레드 ExecutorService 초기화 /이를 통해 카메라 프레임 처리 및 이미지 분석 같은 작업을 비동기적으로 처리
        cameraProviderFuture =
            ProcessCameraProvider.getInstance(this) // CameraX의 ProcessCameraProvider 인스턴스 가져오기 /카메라의 생명 주기 관리 하는데 사용 / getInstance는 새로운 길 즉 새로은 쓰레드가 만들어 지는 모양이다.
        analysis = MyImageAnalyzer() // MyImageAnalyzer 인스턴스 생성

        cameraProviderFuture.addListener({  // CameraProviderFuture가 완료되었을 때 실행할 작업 추가 //람다로 Runnable생략 되어있음
            try {
                Log.d("cameraProviderFuture", "CameraProviderFuture 완료")
                val processCameraProvider: ProcessCameraProvider =
                    cameraProviderFuture.get()  //  비동기 작업이 완료될 때까지 현재 스레드를 차단합니다. 즉, ProcessCameraProvider 인스턴스가 준비될 때까지 기다립니다.
                bindPreview(processCameraProvider) //카메라 프로바이더 바인딩
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this)) // 메인 스레드에서 실행되도록 설정
    }

    private fun bindPreview(processCameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()  // 미리보기 객체 생성
        preview.surfaceProvider = previewView.surfaceProvider // 미리보기 뷰 설정
        val cameraSelector: CameraSelector = CameraSelector.Builder().requireLensFacing(
            CameraSelector.LENS_FACING_BACK
        ).build() // 후면 카메라 선택
        val imageCapture: ImageCapture = ImageCapture.Builder().build() // 이미지 캡처 객체 생성
        val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder() // 이미지 분석 객체 생성
            .setTargetResolution(Size(1200, 720))  // 분석할 이미지 해상도 설정
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // 백프레셔 전략 설정
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor, analysis) // 이미지 분석기 설정
        processCameraProvider.unbindAll() // 기존 바인딩 해제
        processCameraProvider.bindToLifecycle(
            this, cameraSelector, preview, imageCapture, imageAnalysis
        ) // 생명주기에 바인딩
    }

    inner class MyImageAnalyzer() : ImageAnalysis.Analyzer {
        private lateinit var db: BottomDialog // 바텀 다이어로그
        private var isDialogDisplayed = false // 다이어로그 표시 상태
        override fun analyze(image: ImageProxy) {
            if (isDialogDisplayed) {
                image.close() // 다이어로그가 표시 중이면 이미지를 닫고 분석 중지
                return
            }
            scanBarcode(image) // 바코드 스캔
        }

        // 비트맵 이미지를 분석하는 메서드
        fun analyzeBitmap(bitmap: Bitmap) {
            val inputImage = InputImage.fromBitmap(
                bitmap, 0
            ) // mlkit에서 제공하는 메서드 bitmap 객체와 이미지 각도를 받아 inputImage생성
            scanBarcode(inputImage) { //{}식이 두번째 매개 변수로 들어 간다. 람다식
                Log.d("scanBarcodeFromBitmap", "바코드 스캔 완료") //onComplete() 콜백 부분
            }
        }

        @OptIn(ExperimentalGetImage::class)
        private fun scanBarcode(
            image: Any, // Any 타입으로 ImageProxy 또는 InputImage를 받을 수 있게 설정
            onComplete: () -> Unit = {}
        ) {
            val inputImage = when (image) {
                is InputImage -> image
                is ImageProxy -> {
                    val mediaImage = image.image ?: run {
                        image.close() //이미지가 null이면 닫는다.
                        return
                    }
                    InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)//이미지와 각도 설정
                }
                else -> throw IllegalArgumentException("Unsupported image type")
            }

            val barcodeScannerOptions = BarcodeScannerOptions.Builder() // 바코드 스캐너 옵션 설정
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC) // QR 코드 형식 설정 // AZTEC 코드 형식 설정
                .build()

            val scanner = BarcodeScanning.getClient(barcodeScannerOptions) //스캐너 객채 생성

            scanner.process(inputImage) //스캐너
                .addOnSuccessListener { barcodes ->
                    Log.d("scanBarcode", "바코드 인식 성공") //바코드 인식 성공 보단 바코드 인식 준비 성공 느낌
                    readerBarcodeData(barcodes) // 성공적으로 바코드를 읽으면 데이터 처리
                }
                .addOnFailureListener { e ->
                    Log.d("scanBarcode", "바코드 인식 실패")
                    e.printStackTrace() // 실패 시 예외 처리 //여기로 들어오는 경우는 못봄
                }
                .addOnCompleteListener {
                    onComplete()// 완료 후 콜백 호출
                    if (image is ImageProxy) {
                        image.close()// 이미지 분석 완료 후 ImageProxy 닫기
                    }
                }
        }

        private fun readerBarcodeData(barcodes: List<Barcode>) { //한번에 여려개 바코드 찍을 수 있으니 list 인거 같다.
            // 바코드 정보 가져오기
            for (barcode in barcodes) {
                val bounds = barcode.boundingBox
                val corners = barcode.cornerPoints

                val rawValue = barcode.rawValue

                val valueType = barcode.valueType
                when (valueType) {
                    Barcode.TYPE_WIFI -> {
                        val ssid = barcode.wifi!!.ssid
                        val password = barcode.wifi!!.password
                        val type = barcode.wifi!!.encryptionType
                    }

                    Barcode.TYPE_URL -> {
                        Log.d("readerBarcodeData", "URL")
                        //Todo 인터페이스로 넘기기
                        val title = barcode.url!!.title
                        val url = barcode.url!!.url
                        bottomDialogDisplayed(url.toString())
                    }

                    Barcode.TYPE_TEXT -> {
                        Log.d("readerBarcodeData", "TEXT")
                        bottomDialogDisplayed(rawValue.toString())
                        //Todo 인터페이스로 넘기기
                    }
                }
            }
        }

        private fun bottomDialogDisplayed(url: String) {
            if (!::db.isInitialized || !db.isAdded) {
                db = BottomDialog()
                db.show(supportFragmentManager, "BottomDialog")
                isDialogDisplayed = true // 다이어로그가 표시된 상태로 설정
                db.dismissListener = {
                    isDialogDisplayed = false // 다이어로그가 닫히면 상태 변경
                }
            }
            db.fetchUrlFunction(url) // URL 데이터를 BottomDialog로 전달
        }
    }

    // URI를 비트맵 이미지로 변환하는 함수
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream) //리턴
        } catch (e: Exception) {
            e.printStackTrace()
            null //예외시 리턴
        }
    }

    private fun permission() {
        val cameraPermissionStatus =
            ContextCompat.checkSelfPermission(applicationContext, "android.permission.CAMERA")
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    val processCameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    bindPreview(processCameraProvider)
                } else {
                    Toast.makeText(applicationContext, "카메라 권한을 허용 해야 합니다.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        if (cameraPermissionStatus != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch("android.permission.CAMERA")
        }
    }
}


class BottomDialog : BottomSheetDialogFragment() {

    interface FinishListener {
        fun finish()
    }

    private var finishListener: FinishListener? = null

    private lateinit var title: TextView
    private lateinit var close: ImageView
    private lateinit var fetchUrl: String

    private lateinit var binding: QrcodeBottomDialogBinding

    var dismissListener: (() -> Unit)? = null //인터페이스 람다식

    //    액티비티에 연결 될 때 호출
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FinishListener) {
            finishListener = context //콜백 인터페이스 연결
        }
    }

    //    액티비티에서 분리될 때 호출
    override fun onDetach() {
        super.onDetach()
        finishListener = null  //인터페이스 연결 해제
        dismissListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = QrcodeBottomDialogBinding.inflate(inflater, container, false)
        title = binding.txtResultUrl
        close = binding.imgCloseBtmDialog

        title.setOnClickListener {
            if (fetchUrl.startsWith("http://") || fetchUrl.startsWith("https://") || fetchUrl.startsWith(
                    "www."
                )
            ) {
                startActivity()
            } else {
                startActivity()
                finishListener?.finish()
            }
            //            if(fetchUrl.startsWith("http://") || fetchUrl.startsWith("https://") || fetchUrl.startsWith("www.")){
//                val intent = Intent(Intent.ACTION_VIEW)
//                intent.data = Uri.parse(fetchUrl)
//
//                startActivity(intent)
//                dismiss()
//            }else{
//                when(fetchUrl){
//                    "TodoMainActivity" ->{ //내 앱 내부의 activity를 qrcode로 실행 하기
//                        val activity = TodoMainActivity::class.java
//                        val intent = Intent(this.requireContext(), activity)
//                        startActivity(intent)
//                        dismiss()
//                        finishListener?.finish()
//                    }
//                }
//            }
        }

        close.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()//다이얼로그가 닫힐때 리스너 호출
    }

    private fun startActivity() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(fetchUrl)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent) //Intent.ACTION name이 android.intent.action.VIEW이고 uri이 fetchUrl인 액티비티 스타트
        } else {
            Toast.makeText(requireActivity(), "유효하지 않은 URL입니다.", Toast.LENGTH_LONG).show() //ACTION NAME이 다르거나 URL이 다른경우
        }
        dismiss()
    }

    fun fetchUrlFunction(url: String) {
        //코루틴이나 쓰레드 안쓰면 fragment생명주기 전에 펑션이 작동해 에러생긴다.
        CoroutineScope(Dispatchers.IO).launch {
            fetchUrl = url
            withContext(Dispatchers.Main) {
                if (::title.isInitialized) {
                    title.text = fetchUrl
                }
            }
        }
//        val executorService: ExecutorService = Executors.newSingleThreadExecutor() // 단일 스레드 풀을 생성합니다. 백그라운드에서 작업을 수행하기 위해 사용됩니다.
//        val handler = Handler(Looper.getMainLooper()) //ui 스레드에서 코드 실행 Looper.getMainLooper()를 통해 메인 스레드의 루퍼를 가져옵니다.  메인 스레드에서 코드를 실행하기 위해 핸들러를 생성합니다. UI 업데이트를 위해 필요합니다.
//        executorService.execute { // 백그라운드 스레드에서 URL을 설정하는 작업을 수행합니다.
//            fetchUrl = url
//            handler.post {
//                if (::title.isInitialized) {
//                    title.text = fetchUrl
//                }
//            }
//        }
    }
}
