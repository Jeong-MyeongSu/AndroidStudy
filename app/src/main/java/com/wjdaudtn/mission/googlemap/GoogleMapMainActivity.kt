package com.wjdaudtn.mission.googlemap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityGoogleMapMainBinding
import com.wjdaudtn.mission.databinding.FragmentGoogleMapBottomDialogSheetBinding
import com.wjdaudtn.mission.databinding.ItemSubBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.floor
import kotlin.properties.Delegates
import com.wjdaudtn.mission.R.id.bottom_sheet_google_map
import com.wjdaudtn.mission.databinding.GoogleImageBinding
import com.wjdaudtn.mission.databinding.NaverImageBinding

/**
 *packageName    : com.wjdaudtn.mission.googlemap
 * fileName       : GoogleMapMainActivity
 * author         : licen
 * date           : 2024-08-09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-09        licen       최초 생성
 */


abstract class Map(
    val minSize: Double, //줌 최소
    val maxSize: Double, //줌 최대
    val activity: GoogleMapMainActivity,
    private val bottomSheetButton: ButtonBottomDialog //버튼 다이어로그
) {
    protected var currentLatitude by Delegates.notNull<Double>() //현재 위도
    protected var currentLongitude by Delegates.notNull<Double>() //현재 경도
    private lateinit var fusedLocationClient: FusedLocationProviderClient //현재 위치 받기 위한 객체

    abstract fun firstMapLocation() //처음 위치로 이동, 마커 찍기
    abstract fun clickMap() //맵 클릭 함수
    abstract fun zoom(zm: Double) //줌 함수

    protected fun bottomButton(lat: Double, lng: Double){
        val fragmentManager = activity.supportFragmentManager
        val existingFragment = fragmentManager.findFragmentByTag("googleMapDialog")

        if (existingFragment == null) {
            bottomSheetButton.show(fragmentManager, "googleMapDialog")
            bottomSheetButton.fetchLatLng(lat, lng)
        } else {
            bottomSheetButton.fetchLatLng(lat, lng)
        }
    }

    @SuppressLint("MissingPermission")
    protected fun startMap(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity) // 위치 받을 객체 초기화
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? -> //위치 받았을 때 리스너 location 현위치
            if (location != null) {
                currentLatitude = location.latitude
                currentLongitude = location.longitude
                firstMapLocation()
                clickMap()

            } else {
                Log.d("TestActivity", "Location is null")
            }
        }.addOnFailureListener { e ->
            Log.e("TestActivity", "Failed to get location", e)
        }
    }
}

class NaverMap(
    minSize: Double,
    maxSize: Double,
    activity: GoogleMapMainActivity,
    bottomSheetButton: ButtonBottomDialog
) : Map(minSize, maxSize, activity, bottomSheetButton), com.naver.maps.map.OnMapReadyCallback {

//    private lateinit var fusedLocationClient: FusedLocationProviderClient //현재 위치 받기 위한 객체
    private var mNaverMap: NaverMap? = null //생명주기에 쓰일 네이버 맵 객체
    private var clickMarker: com.naver.maps.map.overlay.Marker? = null

    //네이버 맵을 사용 준비 되었을 때 호출 되는 메서드 OnMapReadyCallback
    override fun onMapReady(p0: NaverMap) {
        mNaverMap = p0
        mNaverMap!!.minZoom = minSize
        mNaverMap!!.maxZoom = maxSize
        startMap()
    }

    override fun firstMapLocation() {
        val latLng = com.naver.maps.geometry.LatLng(currentLatitude, currentLongitude) //카메라 업데이트를 위한 위도경도 객체
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, 15.0) //현 위치와 줌 상태 업데이트
        mNaverMap?.moveCamera(cameraUpdate) //카메라 이동

        // 마커 추가
        val marker = com.naver.maps.map.overlay.Marker() //마커 객체
        marker.position = latLng // 마커 위치
        marker.map = mNaverMap  // 마커를 지도에 표시
    }

    override fun clickMap() {
        mNaverMap!!.setOnMapClickListener { pointF, latLng ->
            val clickedLatitude = latLng.latitude //클릭 위도 초기화
            val clickedLongitude = latLng.longitude //클릭 경도 초기화
            clickMarker?.map = null //마커 있으면 없앰
            clickMarker = com.naver.maps.map.overlay.Marker() //마커 생성
            clickMarker?.position = latLng //마커 위치
            clickMarker?.iconTintColor = Color.RED //마커 색
            clickMarker?.map = mNaverMap //마커 지도에 표시
            bottomButton(clickedLatitude, clickedLongitude) //마커 위치를 바텀 버튼 으로 보냄
        }
    }

    override fun zoom(zm: Double) {
        val latLng = com.naver.maps.geometry.LatLng(currentLatitude, currentLongitude) //현위치
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, zm) //업데이트 객체 초기화
        mNaverMap?.moveCamera(cameraUpdate) // 카메라 움직임 - 줌
    }
}

class GoogleMap(

    minSize: Double,
    maxSize: Double,
    activity: GoogleMapMainActivity,
    bottomSheetButton: ButtonBottomDialog
) : Map(minSize, maxSize, activity, bottomSheetButton),GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback{

//    private lateinit var providerClient: FusedLocationProviderClient // 위치 불러 오기 위한 객체 생성
    private lateinit var apiClient: GoogleApiClient  //GoogleApiClient를 빌더 패턴 객체 생성
    private var mGoogleMap: GoogleMap? = null //구글 맵 객체 생성
    private var clickMarker: Marker? = null //마커 객체 생성

    //OnMapReadyCallback
    override fun onMapReady(p0: GoogleMap) {
        mGoogleMap = p0 // GoogleMap 객체를 초기화
        mGoogleMap?.setMinZoomPreference(minSize.toFloat()) //최소 줌 레벨 설정
        mGoogleMap?.setMaxZoomPreference(maxSize.toFloat()) //최대 줌 레벨 설정
        connect() //네이버랑 다른점 apiclint로 한번 연결하고 onConnected 호출
    }
    // GoogleApiClient가 성공적으로 연결되었을 때 호출되는 메서드
    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        startMap()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d("onConnectionSuspended","GoogleApiClient의 연결이 일시 중단 되었을 때 호출 되는 메서드")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("onConnectionFailed","GoogleApiClient의 연결이 일시 실패 되었을 때 호출 되는 메서드")
    }

    override fun firstMapLocation() {
        val latLng = LatLng(currentLatitude, currentLongitude) // LatLng 객체를 생성하여 위치를 지정
        val position: CameraPosition = CameraPosition.Builder()
            .target(latLng)  // 지도의 타겟 위치를 설정
            .zoom(16f) // 줌 레벨을 설정
            .build()
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(position)) // 지도의 카메라를 지정한 위치로 이동
        // 마커 옵션을 설정하고 지도에 마커를 추가
        mGoogleMap?.addMarker(MarkerOptions().apply {
            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            position(latLng)
            title("MyLocation")
        })
    }

    override fun clickMap() {
        mGoogleMap!!.setOnMapClickListener { _latLng -> //클릭 위치
            val clickedLatitude = _latLng.latitude //클릭 위도 초기화
            val clickedLongitude = _latLng.longitude //클릭 경도 초기화
            Log.d("clickMap", "클릭 위치: 위도 $clickedLatitude, $clickedLongitude ")
            val latLng = LatLng(clickedLatitude, clickedLongitude)
            clickMarker?.remove() //마크가 있으면 지움
            clickMarker = mGoogleMap?.addMarker(MarkerOptions().apply {
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                position(latLng)
                title("ClickLocation")
            }) // 마커를 지도에 추가
            bottomButton(clickedLatitude, clickedLongitude)
        }
    }

    override fun zoom(zm: Double) {
        val latLng = LatLng(currentLatitude, currentLongitude) // LatLng 객체를 생성하여 위치를 지정
        val position: CameraPosition = CameraPosition.Builder()
            .target(latLng)  // 지도의 타겟 위치를 설정
            .zoom(zm.toFloat()) // 줌 레벨을 설정
            .build()
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(position)) // 카메라 움직임 - 줌
    }
    private fun connect(){
        apiClient = GoogleApiClient.Builder(activity) // GoogleApiClient를 빌더 패턴을 사용해 초기화하고 연결을 시도
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        apiClient.connect()  // GoogleApiClient와의 연결을 시작 ->onMapReady
    }
}

class GoogleMapMainActivity : AppCompatActivity(), ButtonBottomDialog.LatLngListener {
    @SuppressLint("NotifyDataSetChanged")
    override fun saveLatLng(latitude: Double, lngLong: Double) {
        latLngList.add(LatLng(latitude, lngLong))
        adapter.notifyDataSetChanged()
        binding.btnGoogleMapDelete.visibility = View.VISIBLE
    }

    private lateinit var binding: ActivityGoogleMapMainBinding //메인 엑티비티 바인딩
    private lateinit var googleMap: com.wjdaudtn.mission.googlemap.GoogleMap
    private lateinit var naverMap: com.wjdaudtn.mission.googlemap.NaverMap
    private lateinit var kakaoMapFragment: KakaoFragment

    private lateinit var bottomSheetButton: ButtonBottomDialog //바텀바이어로그 버튼
    private lateinit var bottomSheet: BottomSheetBehavior<LinearLayout> //바텀시트 객체 생성
    private lateinit var adapter: LatLngAdapter //리사이클러 뷰 객채 생성
    private lateinit var layoutManager: LinearLayoutManager //레이아웃 매니저 객채 생성
    private lateinit var dividerItemDecoration: DividerItemDecoration //데코레이션 객체 생성

    data class LatLng(var latitude: Double, var lngLong: Double) //좌표 데이터 클래스

    private lateinit var latLngList: MutableList<LatLng> //좌표 리스트


    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoogleMapMainBinding.inflate(layoutInflater)
        permission()
        setContentView(binding.root)

        latLngList = mutableListOf() //좌표 리스트 초기화

        bottomSheet =
            BottomSheetBehavior.from(findViewById(bottom_sheet_google_map))//바텀다이어로그 초기화
        bottomSheetButton = ButtonBottomDialog()//바텀 다이어 로그 버튼

        //리사이클러 뷰 초기화
        val bottomSheetRecyclerView: RecyclerView = findViewById(R.id.recycler_lat_lng)
        layoutManager = LinearLayoutManager(this)
        adapter = LatLngAdapter(latLngList)
        dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        bottomSheetRecyclerView.adapter = adapter
        bottomSheetRecyclerView.layoutManager = layoutManager
        bottomSheetRecyclerView.addItemDecoration(dividerItemDecoration)



        googleMap = GoogleMap(5.0,18.0,this,bottomSheetButton)
        naverMap = NaverMap(5.0, 18.0, this, bottomSheetButton)

        kakaoMapFragment = KakaoFragment(5, 15)

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.mapView, mapFragment)
            .commit()
        mapFragment.getMapAsync(googleMap)//// SupportMapFragment를 찾아 지도가 준비되면 콜백을 통해 알림을 받음

    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //리팩토링
        val menuItem1: MenuItem? = menu?.add(0, 0, 0, "구글")
        menuItem1?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        val menuItem2: MenuItem? = menu?.add(0, 1, 0, "네이버")
        menuItem2?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        val menuItem3: MenuItem? = menu?.add(0, 2, 0, "카카오")
        menuItem3?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

//        val menuItemBindingGoogle = GoogleImageBinding.inflate(layoutInflater)
//        val menuItemBindingNaver = NaverImageBinding.inflate(layoutInflater)
//        menuItem1?.actionView = menuItemBindingGoogle.root
//        menuItem2?.actionView = menuItemBindingNaver.root


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            0 -> {
                val googleMapFragment = SupportMapFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.mapView, googleMapFragment)
                    .commit()
                googleMapFragment.getMapAsync(googleMap)//// SupportMapFragment를 찾아 지도가 준비되면 콜백을 통해 알림을 받음
                true
            }

            1 -> {
                val naverMapFragment = MapFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.mapView, naverMapFragment)
                    .commit()
                naverMapFragment.getMapAsync(naverMap)//// SupportMapFragment를 찾아 지도가 준비되면 콜백을 통해 알림을 받음
                true
            }

            2 -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.mapView, kakaoMapFragment)
                    .commit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        setSupportActionBar(binding.tbGoogleMap)
        supportActionBar?.apply {
            title = "Google Map"
            setDisplayHomeAsUpEnabled(true)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        // WindowManager를 사용하여 화면의 높이를 가져옴
        val wm: WindowManager =
            applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = wm.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        val screenHeight = size.y

        // 최대 높이: 4/3
        val maxHeight = (screenHeight / 4) * 3 - 100
        // 최소 높이: 4/1 (peekHeight로 설정)
        val minHeight = screenHeight / 4

//      바텀 시트 최대 높이 설정
        bottomSheet.maxHeight = maxHeight
        bottomSheet.peekHeight = minHeight
        bottomSheet.isHideable = false // hideable을 false로 설정
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheet.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.d("onSlide", "$newState")
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("onSlide", "$slideOffset")
                val zoomSize = 16.0 - (slideOffset * 16)
                val currentFragment = supportFragmentManager.findFragmentById(R.id.mapView)
                when (currentFragment) {
                    is SupportMapFragment -> googleMap.zoom(zoomSize)
                    is MapFragment -> naverMap.zoom(zoomSize)
                }
            }
        })

        binding.btnGoogleMapDelete.visibility =
            if (latLngList.size != 0) View.VISIBLE else View.GONE
        binding.btnGoogleMapDelete.setOnClickListener {
            latLngList.clear()
            adapter.notifyDataSetChanged()
            binding.btnGoogleMapDelete.visibility =
                if (latLngList.size != 0) View.VISIBLE else View.GONE
        }
    }

    private fun permission() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val writeExternalStorageGranted =
                permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
            val accessNetworkStateGranted =
                permissions[Manifest.permission.ACCESS_NETWORK_STATE] ?: false

            if (!fineLocationGranted || !(writeExternalStorageGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) || !accessNetworkStateGranted) {
                Toast.makeText(applicationContext, "권한 거부됨", Toast.LENGTH_SHORT).show()
                finish()
                Log.d("permission", "ACCESS_FINE_LOCATION: $fineLocationGranted")
                Log.d("permission", "WRITE_EXTERNAL_STORAGE: $writeExternalStorageGranted")
                Log.d("permission", "ACCESS_NETWORK_STATE: $accessNetworkStateGranted")
            }
        }
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE
                )
            )
        }
    }

    class LatLngAdapter(private var item: MutableList<LatLng>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return LatLngViewHolder(
                ItemSubBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return item.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as LatLngViewHolder).bind()
        }

        inner class LatLngViewHolder(val binding: ItemSubBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind() {
                item.let {
                    val mItem = item[adapterPosition]
                    binding.itemSubData.text = "${mItem.latitude}, ${mItem.lngLong}"
                }
            }
        }
    }
}


class ButtonBottomDialog :
    BottomSheetDialogFragment() {

    interface LatLngListener {
        fun saveLatLng(latitude: Double, lngLong: Double)
    }

    private var onLatLngListener: LatLngListener? = null
    private lateinit var binding: FragmentGoogleMapBottomDialogSheetBinding

    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()
    private lateinit var latLng: TextView


//    private lateinit var _latLngList: MutableList<GoogleMapMainActivity.LatLng>


    //    액티비티에 연결 될 때 호출
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LatLngListener) {
            onLatLngListener = context //콜백 인터페이스 연결
        }
    }

    //    액티비티에서 분리될 때 호출
    override fun onDetach() {
        super.onDetach()
        onLatLngListener = null  //인터페이스 연결 해제
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoogleMapBottomDialogSheetBinding.inflate(inflater, container, false)
        latLng = binding.txtGoogleMapBottomDialogLatLng

        binding.btnGoogleMapBottomDialog.setOnClickListener {
            onLatLngListener?.saveLatLng(latitude, longitude)
            dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // BottomSheet 크기 조정
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = 100.dpToPx(requireContext())
        bottomSheet?.layoutParams?.width = 200.dpToPx(requireContext())

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isHideable = false

            val layoutParams = it.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.gravity = Gravity.END
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismiss()
    }

    // dp를 px로 변환하는 확장 함수
    private fun Int.dpToPx(context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (this * density).toInt()
    }


    @SuppressLint("SetTextI18n")
    fun fetchLatLng(lat: Double, lng: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            latitude = floor(lat * 100_00) / 100_00
            longitude = floor(lng * 100_00) / 100_00
            withContext(Dispatchers.Main) {
                if (::latLng.isInitialized) {
                    latLng.text = "$latitude, $longitude"
                }
            }
        }
    }
}