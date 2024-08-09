package com.wjdaudtn.mission.installApp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.wjdaudtn.mission.databinding.ActivityInstallAppBinding
import com.wjdaudtn.mission.databinding.ItemInstallAppBinding
import com.wjdaudtn.mission.qrCode.QrCodeMainActivity


abstract class PackageInfo(var packageInfoList: List<PackageInfo>, val context: Context,val packageManager: PackageManager) {
    //버튼 스위치
    private var isAppNameSwitch:Boolean = false
    private var isCategorySwitch:Boolean = false

    //정렬
    fun appNameSort(){
        packageInfoList = sort()
    }
    fun categoryNameSort(){
        packageInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // api 이하는 category 정보가 없는듯 하다.
            if(!isCategorySwitch){
                packageInfoList.sortedBy {
                    val categoryTitle = ApplicationInfo.getCategoryTitle(context, it.applicationInfo.category) // category 이름 순 정렬
                    categoryTitle?.toString() ?: ""
                }
            }else{
                packageInfoList.sortedBy {
                    val categoryTitle = ApplicationInfo.getCategoryTitle(context, it.applicationInfo.category) //역순 정렬
                    categoryTitle?.toString() ?: ""
                }.reversed()
            }
        } else {
            sort()
        }
        isCategorySwitch = !isCategorySwitch
    }
    private fun sort():List<PackageInfo>{
         val result = if(!isAppNameSwitch){
            packageInfoList.sortedBy { it.applicationInfo.loadLabel(packageManager).toString() } //appName 이름 순 정렬
        }else{
            packageInfoList.sortedBy { it.applicationInfo.loadLabel(packageManager).toString() }.reversed() // reversed 역순 정렬
        }
        isAppNameSwitch = !isAppNameSwitch
        return result
    }
}

class InstallAppActivity : AppCompatActivity() {
    //메인 엑티비티 바인딩 선언
    private lateinit var binding: ActivityInstallAppBinding
    //리사이클러뷰 부분 선언부
    private lateinit var adapter: InstallRecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var dividerItemDecoration:DividerItemDecoration
    //패키지 정보 선언부
    private lateinit var resultPackageInfo: com.wjdaudtn.mission.installApp.PackageInfo


    @SuppressLint("QueryPermissionsNeeded", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInstallAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultPackageInfo = object : com.wjdaudtn.mission.installApp.PackageInfo(packageManager.getInstalledPackages(0),baseContext,packageManager){}//패키지 정보 객체
    }

    override fun onResume() {
        super.onResume()
        initView()
        // 알림 접근 권한이 부여되지 않았는지 확인.
//        if (!isNotificationServiceEnabled()) {
//            // 사용자를 알림 접근 설정 화면으로 보내는 Intent를 생성.
//            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
//            // 설정 화면을 열어 사용자가 권한을 부여할 수 있도록 함.
//            startActivity(intent)
//        }
    }
//    // 알림 접근 권한이 부여되었는지 확인하는 함수
//    private fun isNotificationServiceEnabled(): Boolean {
//        // MyNotificationListener 클래스의 ComponentName 객체를 생성
//        val cn = ComponentName(this, MyNotificationListener::class.java)
//        // 'enabled_notification_listeners' 설정 값을 가져옴
//        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
//        // 설정 값이 null이 아니고, MyNotificationListener의 ComponentName이 포함되어 있으면 true를 반환
//        return flat != null && flat.contains(cn.flattenToString())
//    }
    private fun initView(){
        setSupportActionBar(binding.installAppToolBar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) //업버튼
            title = "Install App"
        }
        onBackPressedDispatcher.addCallback(
            QrCodeMainActivity(),
            object : OnBackPressedCallback(true) { //뒤로가기 버튼
                override fun handleOnBackPressed() {
                    finish()
                }
            })
        layoutManager = LinearLayoutManager(baseContext) //리사이클러뷰 리니어레이아웃매니저
        adapter = InstallRecyclerView(resultPackageInfo) //어댑터 객체 초기화
        dividerItemDecoration = DividerItemDecoration(baseContext, layoutManager.orientation) //밑줄 만들어주는 기본 데코
        binding.recyclerViewInstallApp.layoutManager = layoutManager
        binding.recyclerViewInstallApp.adapter = adapter
        binding.recyclerViewInstallApp.addItemDecoration(dividerItemDecoration)

        binding.btnInstallApp1.setOnClickListener{ selectSortBtn(1) }
        binding.btnInstallApp2.setOnClickListener { selectSortBtn(2) }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun selectSortBtn(btnNum:Int){
        when(btnNum){
            1 -> resultPackageInfo.appNameSort()
            2 -> resultPackageInfo.categoryNameSort()
        }
        adapter.notifyDataSetChanged()
    }
}

class InstallRecyclerView(var item: com.wjdaudtn.mission.installApp.PackageInfo): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return InstallViewHolder(ItemInstallAppBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as InstallViewHolder).bind()
    }

    override fun getItemCount(): Int = item.packageInfoList.size

    inner class InstallViewHolder(var binding: ItemInstallAppBinding): ViewHolder(binding.root) {
        fun bind(){
            item.packageInfoList.let{
                val mItem = item.packageInfoList[adapterPosition]

                //text
                if(mItem.applicationInfo.loadLabel(item.packageManager)=="Filled"){ //app 이름 filled 면 retrun 으로 나감
                    return
                }else{
                    binding.txtItemInstallAppName.text = "App Name: ${mItem.applicationInfo.loadLabel(item.packageManager)}" //app 이름
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding.txtItemInstallCategory.text = "Category:${ApplicationInfo.getCategoryTitle(item.context,mItem.applicationInfo.category)}" // category
                }

                // 애플리케이션 아이콘을 PackageManager를 사용하여 가져옴
                val iconDrawable = item.packageManager.getApplicationIcon(mItem.packageName) // 아이콘 객체 초기화

                // 아이콘을 Bitmap으로 변환하여 크기 조정
                val scaledDrawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 아이콘 객체를 bitmap 으로 변환 후 크기조정해서 Drawable 로 변환 후 초기화
                    when (iconDrawable) {
                        is BitmapDrawable -> { //아이콘이 bitmap 형일때
                            val bitmap = iconDrawable.bitmap //bitmap 객체 초기화
                            imageSetting(bitmap) //bitmap 이미지를 크기 조정해서 drawable로 변환
                        }
                        is AdaptiveIconDrawable -> { //아이콘이 AdaptiveIcon 형일때
                            val bitmap = getBitmapFromDrawable(iconDrawable) //비트맵으로 변환하여 bitmap 객체 초기화
                            imageSetting(bitmap)
                        }
                        else -> iconDrawable // 기본 아이콘 drawable
                    }
                } else {
                    iconDrawable
                }
                // 크기 조정된 아이콘을 ImageView에 설정
                binding.imgItemInstallApp.setImageDrawable(scaledDrawable)

            }
        }
    }
    // AdaptiveIconDrawable을 Bitmap으로 변환하는 함수
    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888) //Drawable의 고유 너비와 높이를 기반으로 빈 Bitmap 객체를 생성
        val canvas = Canvas(bitmap) //bitmap 객체 기반 canvas 객체 생성
        drawable.setBounds(0, 0, canvas.width, canvas.height) // Drawable 객체의 경계를 설정합니다. 여기서 setBounds(0, 0, canvas.width, canvas.height)는 Drawable을 Canvas의 전체 영역에 맞춤  이 작업을 통해 Drawable이 Canvas의 크기와 동일한 크기로 설정됨
        drawable.draw(canvas)//Drawable 객체를 설정된 경계 내에서 Canvas 에 그림 Canvas에 그려진 내용은 연결된 Bitmap에 저장 어떻게 이런게 가능하지
        return bitmap
    }
    fun imageSetting(bitmap: Bitmap): Drawable{
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false) //bitmap 이미지 크기 조정후 초기화
        return BitmapDrawable(item.context.resources, scaledBitmap) //bitmap 이미지를 drowable로 변환
    }
}

//    private fun permission(){
//        val permissionStatus = ContextCompat.checkSelfPermission(applicationContext,"android.permission.QUERY_ALL_PACKAGES")
//        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
//            if (!isGranted) {
//                Toast.makeText(applicationContext, "QUERY_ALL_PACKAGES 퍼미션을 허용 해야 합니다.", Toast.LENGTH_SHORT)
//                    .show()
//                finish()
//            }
//        }
//
//        if(permissionStatus != PackageManager.PERMISSION_GRANTED){
//            requestPermissionLauncher.launch("android.permission.QUERY_ALL_PACKAGES")
//        }
//    }