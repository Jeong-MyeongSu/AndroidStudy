package com.wjdaudtn.mission.googlemap

/**
 *packageName    : com.wjdaudtn.mission.googlemap
 * fileName       : KakaoMapSdkApp
 * author         : licen
 * date           : 2024-08-14
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-14        licen       최초 생성
 */
import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao 지도 SDK 초기화
        KakaoMapSdk.init(this, "ec42570d5cf83783b30a24d34a4f3c3a")
    }
}