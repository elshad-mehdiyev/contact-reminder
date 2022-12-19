package com.iremeber.rememberfriends.utils.alarmmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.iremeber.rememberfriends.R
import com.iremeber.rememberfriends.di.HiltAndroidApp
import com.iremeber.rememberfriends.ui.MainActivity

fun showNotification(
    context: Context,
    channelId: String,
    channelName: String,
    notificationId: Int,
    contentTitle: String
) {
    val startAppIntent = Intent(context, MainActivity::class.java)
    val startAppPendingIntent = PendingIntent.getActivity(
        context,
        0,
        startAppIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val deleteIntent = Intent(context, AlarmNotificationDismissedBroadcastReceiver::class.java)
    val deletePendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        deleteIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(contentTitle)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setFullScreenIntent(startAppPendingIntent, true)
        .setDeleteIntent(deletePendingIntent)
    val notification = notificationBuilder.build()
    val notificationManager = context.getSystemService(NotificationManager::class.java)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(channelId) == null
    ) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
        )
    }
    notificationManager.notify(notificationId, notification)
}
class AlarmNotificationDismissedBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmRingtoneState = (context.applicationContext as HiltAndroidApp).alarmRingtoneState
        alarmRingtoneState.value?.stop()
        alarmRingtoneState.value = null
        println("alarm stop")
    }
}