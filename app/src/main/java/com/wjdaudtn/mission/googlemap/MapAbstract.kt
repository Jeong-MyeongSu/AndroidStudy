package com.wjdaudtn.mission.googlemap

/**
 *packageName    : com.wjdaudtn.mission.googlemap
 * fileName       : MapAbstract
 * author         : licen
 * date           : 2024-08-17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-17        licen       최초 생성
 */
abstract class MapAbstract(
    val minSize: Double, //줌 최소
    val maxSize: Double, //줌 최대
    val activity: GoogleMapMainActivity,
    val latLngList: MutableList<LatLng>
) {
    abstract fun initMap() //현재 위치 초기화 및 초기 위치 설정, 클릭 함수 사용
    abstract fun firstMapLocation() //처음 위치로 이동, 마커 찍기
    abstract fun clickMap() //맵 클릭 함수
    abstract fun zoom(zm: Double) //줌 함수
}