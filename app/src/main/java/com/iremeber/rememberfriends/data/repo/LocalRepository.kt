package com.iremeber.rememberfriends.data.repo

import androidx.lifecycle.LiveData
import com.iremeber.rememberfriends.data.local.ContactDao
import com.iremeber.rememberfriends.data.models.enums.DataSourceType
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.models.enums.UpdateSourceType
import com.iremeber.rememberfriends.domain.update_entities.UpdateReminderCardAfterAlarmTriggerModel
import com.iremeber.rememberfriends.domain.update_entities.UpdateReminderCardModel
import com.iremeber.rememberfriends.domain.update_entities.UpdateScheduleAlarmModel
import javax.inject.Inject


class LocalRepository @Inject constructor(
    private val dao: ContactDao
) {
    suspend fun saveData(
        contact: FavoriteContactModel? = null,
        scheduleAlarmModel: ScheduleAlarmModel? = null,
        source: DataSourceType
    ) {
        when (source) {
            DataSourceType.FAVORITE -> contact?.let { dao.saveToFavorites(it) }
            DataSourceType.SCHEDULE -> scheduleAlarmModel?.let { dao.saveToScheduleAlarm(it) }
        }
    }

    suspend fun deleteData(requestCode: Int, source: DataSourceType) {
        when (source) {
            DataSourceType.FAVORITE -> dao.deleteFromFavorites(requestCode)
            DataSourceType.SCHEDULE -> dao.deleteFromScheduleAlarmModel(requestCode)
        }
    }

    fun getAllFromFavorites(): LiveData<List<FavoriteContactModel>> {
        return dao.getAllFromFavorites()
    }

    fun getAllFromScheduleAlarmModel(): List<ScheduleAlarmModel> {
        return dao.getAllFromScheduleAlarmModel()
    }

    suspend fun updateData(
        reminderCard: UpdateReminderCardModel? = null,
        ReminderAfterAlarm: UpdateReminderCardAfterAlarmTriggerModel? = null,
        updateScheduleAlarmModel: UpdateScheduleAlarmModel? = null,
        updateDataSourceType: UpdateSourceType
    ) {
        when (updateDataSourceType) {
            UpdateSourceType.REMINDER_CARD -> {
                if (reminderCard != null) {
                    dao.updateReminderCard(
                        reminderCard.date, reminderCard.interval,
                        reminderCard.beginHour, reminderCard.endHour, reminderCard.dateMessage,
                        reminderCard.intervalMessage, reminderCard.requestCode
                    )
                }
            }
            UpdateSourceType.REMINDER_CARD_AFTER_ALARM_TRIGGER -> {
                if (ReminderAfterAlarm != null) {
                    dao.updateReminderCardAfterAlarmTrigger(
                        ReminderAfterAlarm.date,
                        ReminderAfterAlarm.requestCode,
                        ReminderAfterAlarm.dateMessage
                    )
                }
            }
            UpdateSourceType.SCHEDULE -> {
                if (updateScheduleAlarmModel != null) {
                    dao.updateScheduleAlarm(
                        updateScheduleAlarmModel.newTimeInMillis,
                        updateScheduleAlarmModel.requestCode,
                        updateScheduleAlarmModel.interval
                    )
                }
            }
        }
    }
}