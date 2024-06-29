package com.wjdaudtn.mission.Todo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.wjdaudtn.mission.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm received!(notification)")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "todo_channel"
        val channelName = "Todo Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)

        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.bell_alarm)
            .setContentTitle("${intent.getStringExtra("todoTitle")}")
            .setContentText("${intent.getStringExtra("todoContent")}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(0, notification)

        // 알람이 울렸다는 브로드캐스트 전송
        val alarmIntent = Intent("com.example.todo.ALARM_TRIGGERED")
        alarmIntent.putExtra("todoId", intent.getIntExtra("todoId", -1))
        LocalBroadcastManager.getInstance(context).sendBroadcast(alarmIntent)
    }
}