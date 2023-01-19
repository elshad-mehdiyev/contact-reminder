package com.iremeber.rememberfriends.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel

@Dao
interface ContactDao {
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

}