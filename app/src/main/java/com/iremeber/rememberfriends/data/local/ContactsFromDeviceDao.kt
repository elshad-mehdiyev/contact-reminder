package com.iremeber.rememberfriends.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iremeber.rememberfriends.data.models.device_entities.AllContactModel

@Dao
interface ContactsFromDeviceDao {
    @Insert
    suspend fun saveAllList(list: ArrayList<AllContactModel>)
    @Query("DELETE FROM AllContactModel")
    suspend fun deleteAll()
    @Query("SELECT * FROM AllContactModel")
    fun getAllContact(): LiveData<List<AllContactModel>>
    @Query("SELECT COUNT(*) FROM AllContactModel")
    suspend fun isEmpty(): Int
}