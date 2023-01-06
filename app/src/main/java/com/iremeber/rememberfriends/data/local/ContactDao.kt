package com.iremeber.rememberfriends.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel

@Dao
interface ContactDao {
    /**
     * FavoriteContact  table
     */
    @Insert
    suspend fun saveToFavorites(contact: FavoriteContactModel)
    @Query("DELETE FROM FavoriteContactModel WHERE requestCode =:requestCode")
    suspend fun deleteFromFavorites(requestCode: Int)
    @Query("SELECT * FROM FavoriteContactModel")
    fun getAllFromFavorites(): LiveData<List<FavoriteContactModel>>
    @Query("UPDATE FavoriteContactModel SET date =:date, interval =:interval " +
            " , startHour =:beginHour , endHour =:endHour, dateMessage =:dateMessage, " +
            "intervalMessage =:intervalMessage WHERE requestCode =:requestCode")
    suspend fun updateReminderCard(date: String,interval: String,beginHour: String,
                                   endHour:String,dateMessage: String,
                                   intervalMessage: String, requestCode: Int)
    @Query("UPDATE FavoriteContactModel SET date =:date, dateMessage =:dateMessage WHERE requestCode =:requestCode")
    suspend fun updateReminderCardAfterAlarmTrigger(date: String, requestCode: Int, dateMessage: String)
    /**
     * ScheduleAlarmModel Table
     */
    @Insert
    suspend fun saveToScheduleAlarm(scheduleAlarmModel: ScheduleAlarmModel)

    @Query("DELETE FROM ScheduleAlarmModel WHERE requestCode =:requestCode")
    suspend fun deleteFromScheduleAlarmModel(requestCode: Int)

    @Query("SELECT * FROM ScheduleAlarmModel")
    fun getAllFromScheduleAlarmModel(): List<ScheduleAlarmModel>

    @Query("UPDATE ScheduleAlarmModel SET timInMillis =:newTimeInMillis, interval =:interval WHERE requestCode =:requestCode")
    suspend fun updateScheduleAlarm(newTimeInMillis: Long, requestCode: Int, interval: Int)

    @Query("UPDATE ScheduleAlarmModel SET timInMillis =:newTimeInMillis, interval =:interval WHERE requestCode =:requestCode")
    suspend fun updateScheduleAlarmAfterAlarmTrigger(newTimeInMillis: Long, requestCode: Int, interval: Int)
}