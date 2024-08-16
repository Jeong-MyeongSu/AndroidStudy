package com.wjdaudtn.mission.googlemap

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.databinding.FragmentKakaoBinding
import com.wjdaudtn.mission.todo.util.Const.Companion.KAKAO_API_KEY
import kotlin.properties.Delegates

class KakaoFragment( private val minSize:Int, private val maxSize:Int) : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding:FragmentKakaoBinding
    private lateinit var kakaoMapView: MapView
    private lateinit var mKakaoMap: KakaoMap
    private var currentLatitude by Delegates.notNull<Double>()
    private var currentLongitude by Delegates.notNull<Double>()

    private lateinit var styles: LabelStyles
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("KakaoFragment", "onCreateView called")
        binding = FragmentKakaoBinding.inflate(inflater, container, false)
        kakaoMapView = binding.mapViewKakao

        try {
            Log.d("KakaoFragment", "Initializing KakaoMapView")
            kakaoMapView.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    Log.d("KakaoFragment", "onMapDestroy")
                }

                override fun onMapError(error: Exception?) {
                    Log.d("KakaoFragment", "onMapError", error)
                }

            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(kakaoMap: KakaoMap) {
                    Log.d("KakaoFragment", "onMapReady")
                    mKakaoMap = kakaoMap
//                    getLastLocation()
                }
            })
        } catch (e: UnsatisfiedLinkError) {
            Log.e("KakaoFragment", "UnsatisfiedLinkError: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("KakaoFragment", "Exception: ${e.message}", e)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        kakaoMapView.resume()
    }

    override fun onPause() {
        super.onPause()
        kakaoMapView.pause()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if(location != null){
                currentLatitude = location.latitude
                currentLongitude = location.longitude
                firstMap(location)
            }

        }
    }
    //    private fun firstMap(location: Location){
//        val currentLatitude = location.latitude
//        val currentLongitude = location.longitude
//        val latLng = com.kakao.vectormap.LatLng.from(currentLatitude,currentLongitude)
//        val cameraUpdate = CameraUpdateFactory.newCenterPosition(latLng,15)
//        mKakaoMap.moveCamera(cameraUpdate)
//
//
//        val label = Label.NO_CHANGE
//        val labelOption = LabelOptions.from(latLng)
//        val marker = MapPoints.fromLatLng(latLng)
//    }
    // 해당위치로 이동, 라벨 표시
    private fun firstMap(location: Location) {

        val latitude = location.latitude
        val longitude = location.longitude
        val latitude_formatter = String.format("%.6f", latitude).toDouble()  // %.6f는 소수점 이하 6자리까지만 표시
        val longitude_formatter = String.format("%.6f", longitude).toDouble()

        // 이동할 위치(위도,경도) 설정
        val camera = CameraUpdateFactory.newCenterPosition(LatLng.from(latitude_formatter, longitude_formatter))
        // 해당위치로 지도 이동
//        kakaoMap?.moveCamera(camera)
        mKakaoMap.moveCamera(camera, CameraAnimation.from(500,true,true))     // 애니메이션 적용해서 이동


        // 커스텀으로 라벨 생성 및 가져옴
        // 1. LabelStyles 생성 - Icon 이미지 하나만 있는 스타일
        val styles = mKakaoMap.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.baseline_location_pin_24)))

        // styles가 null이 아닐때만, LabelOptions 생성하고 라벨추가
        if(styles != null){
            // 2. LabelOptions 생성
            val options = LabelOptions.from(LatLng.from(latitude_formatter, longitude_formatter)).setStyles(styles)

            // 3. LabelLayer 가져옴 (또는 커스텀 Layer 생성)
            val layer = mKakaoMap.labelManager?.layer

            // 4.options 을 넣어 Label 생성
            layer?.addLabel(options)

        }else{
            Log.e("kakaoMap", "LabelStyles null값 에러")
        }

    }
}