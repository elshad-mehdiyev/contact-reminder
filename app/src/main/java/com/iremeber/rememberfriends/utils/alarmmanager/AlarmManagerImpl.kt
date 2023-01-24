package com.iremeber.rememberfriends.utils.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Base64
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.di.RepositoryEntryPoints
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AlarmManagerImpl(
    val context: Context,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val job = CoroutineScope(SupervisorJob())
    private var scheduleAlarmList = listOf<ScheduleAlarmModel>()
    private val intent = Intent(context, ExactAlarmBroadCastReceiver::class.java)

    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun reScheduleAlarms() {
        val repositoryEntryPoint = EntryPointAccessors.fromApplication(
            context, RepositoryEntryPoints::class.java
        )
        val getFromScheduleModelUseCase = repositoryEntryPoint.getFromScheduleModelUseCase
        job.launch {
            scheduleAlarmList = getFromScheduleModelUseCase()
            if (scheduleAlarmList.isNotEmpty()) {
                for (alarm in scheduleAlarmList) {
                    if (System.currentTimeMillis() < alarm.timInMillis) {
                        setAlarm(
                            timeInMillis = alarm.timInMillis,
                            requestCode = alarm.requestCode,
                            message = alarm.message,
                            interval = alarm.interval,
                        )
                    }
                }
            }
        }
    }

    fun setAlarm(
        timeInMillis: Long,
        requestCode: Int,
        message: String,
        interval: Int
    ) {
        val pendingIntent = createExactAlarmIntent(timeInMillis, requestCode, interval, message)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    private fun createExactAlarmIntent(
        timeInMillis: Long,
        requestCode: Int,
        interval: Int,
        message: String
    ): PendingIntent {
        val encodeTimeInMillis = Base64.encode(timeInMillis.toString().toByteArray(), 0)
        val encodeRequestCode = Base64.encode(requestCode.toString().toByteArray(), 0)
        val encodeInterval = Base64.encode(interval.toString().toByteArray(), 0)
        intent.putExtra("timeInMillis", encodeTimeInMillis)
        intent.putExtra("requestCode", encodeRequestCode)
        intent.putExtra("interval", encodeInterval)
        intent.putExtra("message", message)
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun cancelAlarm(requestCode: Int) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}