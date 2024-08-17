package com.wjdaudtn.mission.googlemap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.MapFragment
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.ActivityGoogleMapMainBinding
import com.wjdaudtn.mission.databinding.ItemSubBinding
import com.wjdaudtn.mission.R.id.bottom_sheet_google_map
import com.wjdaudtn.mission.googlemap.mode.GoogleMap
import com.wjdaudtn.mission.googlemap.mode.NaverMap
import kotlin.math.floor

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

data class LatLng(var latitude: Double, var lngLong: Double) //좌표 데이터 클래스
class GoogleMapMainActivity : AppCompatActivity(), GoogleMap.GoogleOnMarkerPositionListener, NaverMap.NaverOnMarkerPositionListener {

    override fun makerPosition(clickedLatitude: Double, clickedLongitude: Double) {
        latLngList.add(LatLng(clickedLatitude, clickedLongitude))
        adapter.notifyDataSetChanged()
        binding.btnGoogleMapDelete.visibility = View.VISIBLE
    }

    private lateinit var binding: ActivityGoogleMapMainBinding //메인 엑티비티 바인딩

    private lateinit var googleMap: GoogleMap
    private lateinit var naverMap: NaverMap

    private lateinit var bottomSheet: BottomSheetBehavior<LinearLayout> //바텀시트 객체 생성
    private lateinit var adapter: LatLngAdapter //리사이클러 뷰 객채 생성
    private lateinit var layoutManager: LinearLayoutManager //레이아웃 매니저 객채 생성
    private lateinit var dividerItemDecoration: DividerItemDecoration //데코레이션 객체 생성

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

        //리사이클러 뷰 초기화
        val bottomSheetRecyclerView: RecyclerView = findViewById(R.id.recycler_lat_lng)
        layoutManager = LinearLayoutManager(this)
        adapter = LatLngAdapter(latLngList)
        dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        bottomSheetRecyclerView.adapter = adapter
        bottomSheetRecyclerView.layoutManager = layoutManager
        bottomSheetRecyclerView.addItemDecoration(dividerItemDecoration)

        googleMap = com.wjdaudtn.mission.googlemap.mode.GoogleMap(5.0, 18.0, this, latLngList)
        naverMap = NaverMap(5.0, 18.0, this, latLngList)

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
            val currentFragment = supportFragmentManager.findFragmentById(R.id.mapView)
            when (currentFragment) {
                is SupportMapFragment -> googleMap.deleteMarker()
                is MapFragment -> naverMap.deleteMarker()
            }
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
            @SuppressLint("SetTextI18n")
            fun bind() {
                item.let {
                    val mItem = item[adapterPosition]
                    binding.itemSubData.text = "${ floor(mItem.latitude * 100_00) / 100_00}, ${floor(mItem.lngLong * 100_00) / 100_00}"

                }
            }
        }
    }
}
