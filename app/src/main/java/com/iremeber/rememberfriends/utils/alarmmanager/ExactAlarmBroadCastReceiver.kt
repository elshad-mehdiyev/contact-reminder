package com.iremeber.rememberfriends.utils.alarmmanager

import android.content.Context
import android.content.Intent
import com.iremeber.rememberfriends.R
import com.iremeber.rememberfriends.di.HiltAndroidApp
import com.iremeber.rememberfriends.data.repo.ContactRepository
import com.iremeber.rememberfriends.utils.util.Constants.MUSIC_ON_PREFERENCE_KEY
import com.iremeber.rememberfriends.utils.util.Constants.NOTIFICATION_CHANNEL_ID
import com.iremeber.rememberfriends.utils.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.iremeber.rememberfriends.utils.util.Constants.NOTIFICATION_ID
import com.iremeber.rememberfriends.utils.util.UtilsWithContext
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import javax.inject.Inject
import android.util.Base64
import com.iremeber.rememberfriends.utils.util.CommonUtil.getDate


@AndroidEntryPoint
class ExactAlarmBroadCastReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var repository: ContactRepository
    private val job = CoroutineScope(SupervisorJob())
    private var systemLanguage = "en"
    private lateinit var utils: UtilsWithContext

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        systemLanguage = Locale.getDefault().language
        utils = UtilsWithContext(context)
        val alarmManagerImpl = AlarmManagerImpl(context)
        val requestCode = intent.getByteArrayExtra("requestCode")
        val timeInMillis = intent.getByteArrayExtra("timeInMillis")
        val interval = intent.getByteArrayExtra("interval")
        val message =
            intent.getStringExtra("message") ?: context.getString(R.string.have_a_one_notification)
        val decodeTimeInMillis = String(Base64.decode(timeInMillis,0)).toLong()
        val decodeRequestCode = String(Base64.decode(requestCode,0)).toInt()
        val decodeInterval = String(Base64.decode(interval,0)).toInt()
        val newTimeInMillis = decodeTimeInMillis + (decodeInterval.toLong() * 24 * 60 * 60 * 1000)
        var musicOn = 0
        job.launch(Dispatchers.IO) {
            repository.getDataFromDataStore(MUSIC_ON_PREFERENCE_KEY).collectLatest {
                it.let {
                    musicOn = it
                }
            }
        }
        if (decodeInterval > 0) {
            alarmManagerImpl.setAlarm(
                timeInMillis = newTimeInMillis, requestCode = decodeRequestCode,
                interval = decodeInterval, message = message
            )
            val date = getDate(newTimeInMillis, "dd/MM/yyyy")
            println(date)
            val messageDate = date.split("/")
            val updateMessage = when {
                systemLanguage.contains("az") -> {
                    "${messageDate[0]} ${utils.formatMonth(messageDate[1])} " +
                            "${messageDate[2]} tarixi üçün xatırladıcı planlaşdırılıb."
                }
                systemLanguage.contains("tr") -> {
                    "${messageDate[0]} ${utils.formatMonth(messageDate[1])} " +
                            "${messageDate[2]} tarihi için bir hatırlatıcı programlandı."
                }
                else -> {
                    "A reminder is scheduled for the ${messageDate[0]} " +
                            "${utils.formatMonth(messageDate[1])} " + messageDate[2]
                }
            }

            job.launch(Dispatchers.IO) {
                repository.updateReminderCardAfterAlarmTrigger(date, decodeRequestCode, updateMessage)
                repository.updateScheduleAlarmAfterAlarmTrigger(newTimeInMillis, decodeRequestCode, decodeInterval)
            }
        } else {
            job.launch(Dispatchers.IO) {
                repository.deleteFromScheduleAlarmModel(decodeRequestCode)
                repository.deleteFromFavorites(decodeRequestCode)
            }
        }
        showNotification(
            context,
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NOTIFICATION_ID,
            message
        )
        if (musicOn == 1) {
            (context.applicationContext as HiltAndroidApp).apply {
                alarmRingtoneState.value = playRingtone(context)
            }
        }
    }
}