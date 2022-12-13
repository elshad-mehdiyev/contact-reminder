package com.iremeber.rememberfriends.di

import android.app.Application
import android.media.Ringtone
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

@dagger.hilt.android.HiltAndroidApp
class HiltAndroidApp: Application() {
    var alarmRingtoneState: MutableState<Ringtone?> = mutableStateOf(null)
}