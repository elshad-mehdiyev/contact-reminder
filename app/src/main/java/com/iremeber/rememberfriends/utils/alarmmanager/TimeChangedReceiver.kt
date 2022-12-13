package com.iremeber.rememberfriends.utils.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class TimeChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        action?.let {
            if (it == "android.intent.action.TIME_SET") {
                val alarmManagerImpl = AlarmManagerImpl(context)
                alarmManagerImpl.reScheduleAlarms()
            }
        }
    }
}