package com.iremeber.rememberfriends.data.repo

import androidx.lifecycle.LiveData
import com.iremeber.rememberfriends.data.local.ContactDao
import com.iremeber.rememberfriends.data.local.ContactsFromDeviceDao
import com.iremeber.rememberfriends.data.local.ScheduleDao
import com.iremeber.rememberfriends.data.models.enums.DataSourceType
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel
import com.iremeber.rememberfriends.data.models.enums.UpdateSourceType
import com.iremeber.rememberfriends.domain.update_entities.UpdateReminderCardAfterAlarmTriggerModel
import com.iremeber.rememberfriends.domain.update_entities.UpdateReminderCardModel
import com.iremeber.rememberfriends.domain.update_entities.UpdateScheduleAlarmModel
import javax.inject.Inject


class LocalRepository @Inject constructor(
    private val contactDao: ContactDao,
    private val scheduleDao: ScheduleDao,
    private val contactsFromDeviceDao: ContactsFromDeviceDao
) {
    suspend fun saveData(
        contact: FavoriteContactModel? = null,
        scheduleAlarmModel: ScheduleAlarmModel? = null,
        source: DataSourceType
    ) {
        when (source) {
            DataSourceType.FAVORITE -> contact?.let { contactDao.saveToFavorites(it) }
            DataSourceType.SCHEDULE -> scheduleAlarmModel?.let { scheduleDao.saveToScheduleAlarm(it) }
        }
    }

    suspend fun deleteData(requestCode: Int, source: DataSourceType) {
        when (source) {
            DataSourceType.FAVORITE -> contactDao.deleteFromFavorites(requestCode)
            DataSourceType.SCHEDULE -> scheduleDao.deleteFromScheduleAlarmModel(requestCode)
        }
    }

    fun getAllFromFavorites(): LiveData<List<FavoriteContactModel>> {
        return contactDao.getAllFromFavorites()
    }

    fun getAllFromScheduleAlarmModel(): List<ScheduleAlarmModel> {
        return scheduleDao.getAllFromScheduleAlarmModel()
    }
    fun getAllContactFromDb(): LiveData<List<AllContactModel>> {
        return contactsFromDeviceDao.getAllContact()
    }
    suspend fun allContactModelTableIsEmpty(): Int {
        return contactsFromDeviceDao.isEmpty()
    }

    suspend fun saveAllToAllContactModel(list: ArrayList<AllContactModel>) {
        contactsFromDeviceDao.saveAllList(list)
    }
    suspend fun deleteAllFromAllContactModel() {
        contactsFromDeviceDao.deleteAll()
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
                    contactDao.updateReminderCard(
                        reminderCard.date, reminderCard.interval,
                        reminderCard.beginHour, reminderCard.dateMessage,
                        reminderCard.intervalMessage, reminderCard.requestCode
                    )
                }
            }
            UpdateSourceType.REMINDER_CARD_AFTER_ALARM_TRIGGER -> {
                if (ReminderAfterAlarm != null) {
                    contactDao.updateReminderCardAfterAlarmTrigger(
                        ReminderAfterAlarm.date,
                        ReminderAfterAlarm.requestCode,
                        ReminderAfterAlarm.dateMessage
                    )
                }
            }
            UpdateSourceType.SCHEDULE -> {
                if (updateScheduleAlarmModel != null) {
                    scheduleDao.updateScheduleAlarm(
                        updateScheduleAlarmModel.newTimeInMillis,
                        updateScheduleAlarmModel.requestCode,
                        updateScheduleAlarmModel.interval
                    )
                }
            }
        }
    }
}