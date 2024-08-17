package com.wjdaudtn.mission.googlemap.mode

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.wjdaudtn.mission.googlemap.GoogleMapMainActivity
import com.wjdaudtn.mission.googlemap.MapAbstract
import kotlin.properties.Delegates

/**
 *packageName    : com.wjdaudtn.mission.googlemap.mode
 * fileName       : NaverMap
 * author         : licen
 * date           : 2024-08-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-17        licen       최초 생성
 */
class NaverMap(
    minSize: Double,
    maxSize: Double,
    activity: GoogleMapMainActivity,
    latLngList: MutableList<com.wjdaudtn.mission.googlemap.LatLng>
): MapAbstract(minSize, maxSize, activity, latLngList), com.naver.maps.map.OnMapReadyCallback{
    private var mNaverMap: NaverMap? = null //생명주기에 쓰일 네이버 맵 객체
    protected var currentLatitude by Delegates.notNull<Double>() //현재 위도
    protected var currentLongitude by Delegates.notNull<Double>() //현재 경도
    private var clickMarker: com.naver.maps.map.overlay.Marker? = null //마커
    private val markers: MutableList<com.naver.maps.map.overlay.Marker> = mutableListOf() // 마커들을 저장하는 리스트
    private lateinit var fusedLocationClient: FusedLocationProviderClient //현재 위치 받기 위한 객체

    interface NaverOnMarkerPositionListener{
        fun makerPosition(clickedLatitude:Double, clickedLongitude:Double)
    }
    private var makerPositionListener: NaverOnMarkerPositionListener? = null

    @SuppressLint("MissingPermission")
    override fun initMap() { //현재 위치 초기화 및 초기 위치 설정, 클릭 함수
        for(i in latLngList){
            val clickedLatitude = i.latitude //클릭 위도 초기화
            val clickedLongitude = i.lngLong //클릭 경도 초기화
            val latLng = com.naver.maps.geometry.LatLng(clickedLatitude,clickedLongitude)
            clickMarker = com.naver.maps.map.overlay.Marker() //마커 생성
            clickMarker?.position = latLng //마커 위치
            clickMarker?.iconTintColor = Color.RED //마커 색
            clickMarker?.map = mNaverMap //마커 지도에 표시
            markers.add(clickMarker!!) // 리스트에 마커 추가
        }
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(activity) // 위치 받을 객체 초기화
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

    override fun firstMapLocation() { //처음 위치에 카메라 이동하고 마커 찍기
        val latLng =
            com.naver.maps.geometry.LatLng(currentLatitude, currentLongitude) //카메라 업데이트를 위한 위도경도 객체
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, 15.0) //현 위치와 줌 상태 업데이트
        mNaverMap?.moveCamera(cameraUpdate) //카메라 이동

        // 마커 추가
        val marker = com.naver.maps.map.overlay.Marker() //마커 객체
        marker.position = latLng // 마커 위치
        marker.map = mNaverMap  // 마커를 지도에 표시

    }

    override fun clickMap() { //맵 클릭 리스너
        mNaverMap!!.setOnMapClickListener { pointF, latLng ->
            val clickedLatitude = latLng.latitude //클릭 위도 초기화
            val clickedLongitude = latLng.longitude //클릭 경도 초기화
//            clickMarker?.map = null //마커 있으면 없앰
            clickMarker = com.naver.maps.map.overlay.Marker() //마커 생성
            clickMarker?.position = latLng //마커 위치
            clickMarker?.iconTintColor = Color.RED //마커 색
            clickMarker?.map = mNaverMap //마커 지도에 표시
            markers.add(clickMarker!!) // 리스트에 마커 추가
            makerPositionListener?.makerPosition(clickedLatitude, clickedLongitude)
        }
    }

    override fun zoom(zm: Double) {
        val latLng = com.naver.maps.geometry.LatLng(currentLatitude, currentLongitude) //현위치
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(latLng, zm) //업데이트 객체 초기화
        mNaverMap?.moveCamera(cameraUpdate) // 카메라 움직임 - 줌
    }

    //네이버 맵을 사용 준비 되었을 때 호출 되는 메서드 OnMapReadyCallback
    override fun onMapReady(p0: NaverMap) {
        makerPositionListener = activity
        mNaverMap = p0
        mNaverMap!!.minZoom = minSize
        mNaverMap!!.maxZoom = maxSize
        initMap()
    }
    fun deleteMarker(){
        for (marker in markers) {
            marker.map = null
        }
        markers.clear() // 리스트 초기화
    }
}