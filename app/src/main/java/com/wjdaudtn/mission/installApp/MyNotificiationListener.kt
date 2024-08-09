package com.wjdaudtn.mission.installApp

import android.app.Notification
import android.content.pm.ApplicationInfo
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 *packageName    : com.wjdaudtn.mission.installApp
 * fileName       : MyNotificiationListener
 * author         : licen
 * date           : 2024-08-08
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-08        licen       최초 생성
 */
class MyNotificationListener : NotificationListenerService(){

    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        val notification = sbn?.notification

        val title = notification?.extras?.getString(Notification.EXTRA_TITLE)   //제목
        val bigTitle = notification?.extras?.getString(Notification.EXTRA_TITLE_BIG)    //큰 Noti 제목

        val content = notification?.extras?.getString(Notification.EXTRA_TEXT)  //내용
        val bigContent = notification?.extras?.getString(Notification.EXTRA_BIG_TEXT)   //큰 Noti 내용

        val additionalInfo = notification?.extras?.getString(Notification.EXTRA_INFO_TEXT)  //Noti 추가 정보

        //val message = notification?.extras?.getString(Notification.EXTRA_MESSAGES?:"None")    //메세지...메세지가 없을 때 출력하지 않도록 하는 코드 필요
        val category = notification?.category   //알림 카테고리
        val from = sbn?.packageName     //알림을 보낸 어플 패키지 명
        val group = notification?.group     //알림이 속한 그룹명
        val conversationTitle = notification?.extras?.getString(Notification.EXTRA_CONVERSATION_TITLE)  //알림의 대화 제목
        val subText = notification?.extras?.getString(Notification.EXTRA_SUB_TEXT)  //알림의 third party 내용(카카오톡의 경우 단체톡방 이름)
        val largeIcon = notification?.getLargeIcon()    //알림 큰 아이콘
        val smallIcon = notification?.smallIcon     //알림 작은 아이콘

        //로그로 확인
        Log.d("noti", "category: "+category+" | title: "+title+", content: "+content+
                " | bigTitle: "+bigTitle+", bigContent: "+bigContent+" | info: "+additionalInfo+//" | message: "+message+
                " | from: "+from+" | group: "+group+" | conversationTitle: "+conversationTitle+
                " | subText: "+subText)

        println("large: "+largeIcon)
        println("small: "+smallIcon)

        //로그로 package 정보 확인
        val pm = applicationContext.packageManager
        val info : ApplicationInfo = packageManager.getApplicationInfo(from!!, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("appInfo", "app name: "+info.loadLabel(packageManager).toString()+    //어플 이르
                    " | category: "+ ApplicationInfo.getCategoryTitle(this, info.category))
        }  //어플 카테고리

    }

}