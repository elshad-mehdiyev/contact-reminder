package com.iremeber.rememberfriends.data.repo

import androidx.lifecycle.LiveData
import com.iremeber.rememberfriends.data.models.AllContactModel
import com.iremeber.rememberfriends.data.models.AllRingtonesModel
import com.iremeber.rememberfriends.data.models.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.ScheduleAlarmModel
import kotlinx.coroutines.flow.Flow

interface ContactRepositoryInterface {

    /**
     * All Contacts  Table
     */
    suspend fun saveContacts(list: List<AllContactModel>)


    fun getAllContact(): LiveData<List<AllContactModel>>

    suspend fun getContactFromDevice(): ArrayList<AllContactModel>
    /**
     * FavoriteContact  Table
     */
    suspend fun saveToFavorites(contact: FavoriteContactModel)

    suspend fun deleteFromFavorites(requestCode: Int)

    fun getAllFromFavorites(): LiveData<List<FavoriteContactModel>>
    suspend fun updateReminderCard(date: String,interval: String,beginHour: String,
                                   endHour:String,dateMessage: String,
                                   intervalMessage: String, requestCode: Int)
    suspend fun updateReminderCardAfterAlarmTrigger(date: String, requestCode: Int, dateMessage: String)
    /**
     * ScheduleAlarmModel
     */
    suspend fun saveToScheduleAlarm(scheduleAlarmModel: ScheduleAlarmModel)

    suspend fun deleteFromScheduleAlarmModel(requestCode: Int)

    fun getAllFromScheduleAlarmModel(): List<ScheduleAlarmModel>

    suspend fun updateScheduleAlarm(newTimeInMillis: Long, requestCode: Int, interval: Int)

    suspend fun updateScheduleAlarmAfterAlarmTrigger(newTimeInMillis: Long, requestCode: Int, interval: Int)

    /**
     * DataStore
     */
    suspend fun saveToDataStore(key: String, value: Int)
    suspend fun getDataFromDataStore(key: String): Flow<Int>
    /**
     * All  Ringtones
     */
    suspend fun getAllRingtonesFromDevice(): ArrayList<AllRingtonesModel>
}