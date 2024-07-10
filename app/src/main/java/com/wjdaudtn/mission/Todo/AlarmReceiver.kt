package com.wjdaudtn.mission.todo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.wjdaudtn.mission.R
import com.wjdaudtn.mission.todo.util.Const.Companion.ALARM_RECEIVER_ACTION
import com.wjdaudtn.mission.todo.util.Const.Companion.DEFAULT_VALUE
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_CONTENT
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_ID
import com.wjdaudtn.mission.todo.util.Const.Companion.RESULT_KEY_TITLE

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
//        Log.d("AlarmReceiver", "Alarm received!(notification)")
        val showActivityIntent = Intent(context, TodoMainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, showActivityIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "todo_channel"
        val channelName = "Todo Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.bell_alarm)
            .setContentTitle("${intent.getStringExtra(RESULT_KEY_TITLE)}")
            .setContentText("${intent.getStringExtra(RESULT_KEY_CONTENT)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)

//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val notificationBuilder: NotificationCompat.Builder
//        val channelId = "todo_channel"
//        val channelName = "Todo Notifications"
//        val showActivityIntent = Intent(context, TodoMainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(context, 0, showActivityIntent, PendingIntent.FLAG_IMMUTABLE)
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
//            notificationManager.createNotificationChannel(channel)
//            notificationBuilder = NotificationCompat.Builder(context,"ChannelId")
//        }else{
//            notificationBuilder = NotificationCompat.Builder(context)
//        }
//        notificationBuilder.setSmallIcon(R.drawable.bell_alarm)
//        notificationBuilder.setContentTitle("${intent.getStringExtra(RESULT_KEY_TITLE)}")
//        notificationBuilder.setContentText("${intent.getStringExtra(RESULT_KEY_CONTENT)}")
//        notificationBuilder.setContentIntent(pendingIntent)
//        notificationBuilder.setAutoCancel(true)
//
//        notificationManager.notify(0, notificationBuilder.build())

        // 알람이 울렸 다는 broadCast 전송
        val alarmIntent = Intent(ALARM_RECEIVER_ACTION)
        alarmIntent.putExtra(RESULT_KEY_ID, intent.getIntExtra(RESULT_KEY_ID, DEFAULT_VALUE))
        LocalBroadcastManager.getInstance(context).sendBroadcast(alarmIntent)

    }
}