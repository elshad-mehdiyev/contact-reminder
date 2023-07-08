package com.iremeber.rememberfriends.utils.alarmmanager

import android.content.Context
import android.content.Intent
import android.util.Base64
import com.iremeber.rememberfriends.R
import com.iremeber.rememberfriends.data.models.enums.DataSourceType
import com.iremeber.rememberfriends.data.models.enums.UpdateSourceType
import com.iremeber.rememberfriends.di.HiltAndroidApp
import com.iremeber.rememberfriends.domain.interactors.DeleteDataUseCase
import com.iremeber.rememberfriends.domain.interactors.GetDataFromDataStoreUseCase
import com.iremeber.rememberfriends.domain.interactors.UpdateDataUseCase
import com.iremeber.rememberfriends.domain.update_entities.UpdateReminderCardAfterAlarmTriggerModel
import com.iremeber.rememberfriends.domain.update_entities.UpdateScheduleAlarmModel
import com.iremeber.rememberfriends.utils.language.LanguageFactory
import com.iremeber.rememberfriends.utils.util.CommonUtil.getDate
import com.iremeber.rememberfriends.utils.util.Constants.MUSIC_ON_PREFERENCE_KEY
import com.iremeber.rememberfriends.utils.util.Constants.NOTIFICATION_CHANNEL_ID
import com.iremeber.rememberfriends.utils.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.iremeber.rememberfriends.utils.util.Constants.NOTIFICATION_ID
import com.iremeber.rememberfriends.utils.util.date_and_animation.DateAndAnimUtilImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class ExactAlarmBroadCastReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var deleteDataUseCase: DeleteDataUseCase

    @Inject
    lateinit var getDataFromDataStoreUseCase: GetDataFromDataStoreUseCase

    @Inject
    lateinit var updateDataUseCase: UpdateDataUseCase

    private val job = CoroutineScope(SupervisorJob())
    private var systemLanguage = "en"
    private lateinit var utils: DateAndAnimUtilImpl

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        systemLanguage = Locale.getDefault().language
        utils = DateAndAnimUtilImpl()
        val languageSelector = LanguageFactory.languageForKey(context, systemLanguage)
        val alarmManagerImpl = AlarmManagerImpl(context)
        val requestCode = intent.getByteArrayExtra("requestCode")
        val timeInMillis = intent.getByteArrayExtra("timeInMillis")
        val interval = intent.getByteArrayExtra("interval")
        val message =
            intent.getStringExtra("message") ?: context.getString(R.string.have_a_one_notification)
        val decodeTimeInMillis = String(Base64.decode(timeInMillis, 0)).toLong()
        val decodeRequestCode = String(Base64.decode(requestCode, 0)).toInt()
        val decodeInterval = String(Base64.decode(interval, 0)).toInt()
        val newTimeInMillis = decodeTimeInMillis + (decodeInterval.toLong() * 24 * 60 * 60 * 1000)

        if (decodeInterval > 0) {
            alarmManagerImpl.setAlarm(
                timeInMillis = newTimeInMillis, requestCode = decodeRequestCode,
                interval = decodeInterval, message = message
            )
            val date = getDate(newTimeInMillis, "dd/MM/yyyy")
            val messageDate = date.split("/")
            val updateMessage = languageSelector.displayReminderCardDateText(messageDate, utils)
            val updateReminderCardAfterAlarmTriggerModel = UpdateReminderCardAfterAlarmTriggerModel(
                date,
                decodeRequestCode,
                updateMessage
            )
            val updateScheduleAlarmModel = UpdateScheduleAlarmModel(
                newTimeInMillis,
                decodeRequestCode,
                decodeInterval
            )

            job.launch(Dispatchers.IO) {
                updateDataUseCase(
                    reminderAfterAlarm = updateReminderCardAfterAlarmTriggerModel,
                    updateDataSourceType = UpdateSourceType.REMINDER_CARD_AFTER_ALARM_TRIGGER
                )
                updateDataUseCase(
                    updateScheduleAlarmModel = updateScheduleAlarmModel,
                    updateDataSourceType = UpdateSourceType.SCHEDULE
                )
            }
        } else {
            job.launch(Dispatchers.IO) {
                deleteDataUseCase(decodeRequestCode, source = DataSourceType.SCHEDULE)
                deleteDataUseCase(decodeRequestCode, source = DataSourceType.FAVORITE)
            }
        }
        showNotification(
            context,
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NOTIFICATION_ID,
            message
        )
        job.launch(Dispatchers.IO) {
            getDataFromDataStoreUseCase(MUSIC_ON_PREFERENCE_KEY).collect {
                if (it == 1) {
                    (context.applicationContext as HiltAndroidApp).apply {
                        alarmRingtoneState.value = playRingtone(context)
                    }
                } else {
                    (context.applicationContext as HiltAndroidApp).apply {
                        alarmRingtoneState.value?.stop()
                        alarmRingtoneState.value = null
                    }
                }
            }
        }

    }
}