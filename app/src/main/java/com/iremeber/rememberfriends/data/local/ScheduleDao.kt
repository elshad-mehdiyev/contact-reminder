package com.iremeber.rememberfriends.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel

@Dao
interface ScheduleDao {
    @Insert
    suspend fun saveToScheduleAlarm(scheduleAlarmModel: ScheduleAlarmModel)

    @Query("DELETE FROM ScheduleAlarmModel WHERE requestCode =:requestCode")
    suspend fun deleteFromScheduleAlarmModel(requestCode: Int)

    @Query("SELECT * FROM ScheduleAlarmModel")
    fun getAllFromScheduleAlarmModel(): List<ScheduleAlarmModel>

    @Query("UPDATE ScheduleAlarmModel SET timInMillis =:newTimeInMillis, interval =:interval WHERE requestCode =:requestCode")
    suspend fun updateScheduleAlarm(newTimeInMillis: Long, requestCode: Int, interval: Int)
}