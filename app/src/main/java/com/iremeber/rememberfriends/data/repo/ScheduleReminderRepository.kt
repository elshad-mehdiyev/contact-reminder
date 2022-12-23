package com.iremeber.rememberfriends.data.repo

import com.iremeber.rememberfriends.data.local.ContactDao
import com.iremeber.rememberfriends.data.models.ScheduleAlarmModel
import javax.inject.Inject

class ScheduleReminderRepository @Inject constructor(
    private val dao: ContactDao
): IRepository {
    override suspend fun saveToScheduleAlarm(scheduleAlarmModel: ScheduleAlarmModel) {
        dao.saveToScheduleAlarm(scheduleAlarmModel)
    }

    override suspend fun deleteFromScheduleAlarmModel(requestCode: Int) {
        dao.deleteFromScheduleAlarmModel(requestCode)
    }

    override fun getAllFromScheduleAlarmModel(): List<ScheduleAlarmModel> {
        return dao.getAllFromScheduleAlarmModel()
    }

    override suspend fun updateScheduleAlarm(newTimeInMillis: Long, requestCode: Int, interval: Int) {
        dao.updateScheduleAlarm(newTimeInMillis, requestCode, interval)
    }

    override suspend fun updateScheduleAlarmAfterAlarmTrigger(
        newTimeInMillis: Long,
        requestCode: Int,
        interval: Int
    ) {
        dao.updateScheduleAlarmAfterAlarmTrigger(newTimeInMillis, requestCode, interval)
    }
}