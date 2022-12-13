package com.iremeber.rememberfriends.utils.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        action?.let {
            if (it == Intent.ACTION_BOOT_COMPLETED) {
                val alarmManagerImpl = AlarmManagerImpl(context)
                alarmManagerImpl.reScheduleAlarms()
            }
        }
    }
}