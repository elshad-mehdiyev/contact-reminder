package com.iremeber.rememberfriends.data.repo

import com.iremeber.rememberfriends.data.local.ContactDao
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import javax.inject.Inject

class ScheduleReminderRepository @Inject constructor(
    private val dao: ContactDao
) {
    suspend fun saveToScheduleAlarm(scheduleAlarmModel: ScheduleAlarmModel) {
        dao.saveToScheduleAlarm(scheduleAlarmModel)
    }

    suspend fun deleteFromScheduleAlarmModel(requestCode: Int) {
        dao.deleteFromScheduleAlarmModel(requestCode)
    }

    fun getAllFromScheduleAlarmModel(): List<ScheduleAlarmModel> {
        return dao.getAllFromScheduleAlarmModel()
    }

    suspend fun updateScheduleAlarm(newTimeInMillis: Long, requestCode: Int, interval: Int) {
        dao.updateScheduleAlarm(newTimeInMillis, requestCode, interval)
    }
}