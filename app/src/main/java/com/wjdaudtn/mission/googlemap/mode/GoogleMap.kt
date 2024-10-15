package com.wjdaudtn.mission.googlemap.mode

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.wjdaudtn.mission.googlemap.GoogleMapMainActivity
import com.wjdaudtn.mission.googlemap.MapAbstract
import kotlin.properties.Delegates

/**
 *packageName    : com.wjdaudtn.mission.googlemap.mode
 * fileName       : GoogleMap
 * author         : licen
 * date           : 2024-08-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-17        licen       최초 생성
 */
class GoogleMap(
    minSize: Double,
    maxSize: Double,
    activity: GoogleMapMainActivity,
    latLngList: MutableList<com.wjdaudtn.mission.googlemap.LatLng>
): MapAbstract(minSize, maxSize, activity, latLngList), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private var currentLatitude by Delegates.notNull<Double>() //현재 위도
    private var currentLongitude by Delegates.notNull<Double>() //현재 경도
    private lateinit var fusedLocationClient: FusedLocationProviderClient //현재 위치 받기 위한 객체
    private lateinit var apiClient: GoogleApiClient  //GoogleApiClient를 빌더 패턴 객체 생성
    private var mGoogleMap: GoogleMap? = null //구글 맵 객체 생성
    private var clickMarker: Marker? = null //마커 객체 생성
    private val markers:MutableList<Marker> = mutableListOf() // 마커들을 저장하는 리스트

    interface GoogleOnMarkerPositionListener{
        fun makerPosition(clickedLatitude:Double, clickedLongitude:Double)
    }
    private var makerPositionListener: GoogleOnMarkerPositionListener? = null

    @SuppressLint("MissingPermission")
    override fun initMap() {
        //바텀시트에 있는 좌표의 마커를 표시
        for(i in latLngList){
            val clickedLatitude = i.latitude //클릭 위도 초기화
            val clickedLongitude = i.lngLong //클릭 경도 초기화
            val latLng = LatLng(clickedLatitude, clickedLongitude)
            clickMarker = mGoogleMap?.addMarker(MarkerOptions().apply {
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                position(latLng)
                title("ClickLocation")
            }) // 마커를 지도에 추가
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
//            clickMarker?.remove() //마크가 있으면 지움
            clickMarker = mGoogleMap?.addMarker(MarkerOptions().apply {
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                position(latLng)
                title("ClickLocation")
            }) // 마커를 지도에 추가
            markers.add(clickMarker!!) // 리스트에 마커 추가
            makerPositionListener?.makerPosition(clickedLatitude, clickedLongitude)

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

    override fun onConnected(p0: Bundle?) {
        initMap()
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d("onConnectionSuspended", "GoogleApiClient의 연결이 일시 중단 되었을 때 호출 되는 메서드")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.d("onConnectionFailed", "GoogleApiClient의 연결이 일시 실패 되었을 때 호출 되는 메서드")
    }

    override fun onMapReady(p0: GoogleMap) {
        makerPositionListener = activity
        mGoogleMap = p0 // GoogleMap 객체를 초기화
        mGoogleMap?.setMinZoomPreference(minSize.toFloat()) //최소 줌 레벨 설정
        mGoogleMap?.setMaxZoomPreference(maxSize.toFloat()) //최대 줌 레벨 설정
        //네이버랑 다른점 apiclint로 한번 연결하고 onConnected 호출
        apiClient = GoogleApiClient.Builder(activity) // GoogleApiClient를 빌더 패턴을 사용해 초기화하고 연결을 시도
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        apiClient.connect()  // GoogleApiClient와의 연결을 시작 ->onMapReady
    }

    fun deleteMarker(){
        for (marker in markers) {
            marker.remove()
        }
        markers.clear() // 리스트 초기화
    }

}