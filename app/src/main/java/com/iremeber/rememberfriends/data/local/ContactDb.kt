package com.iremeber.rememberfriends.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iremeber.rememberfriends.data.models.AllContactModel
import com.iremeber.rememberfriends.data.models.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.ScheduleAlarmModel

@Database(entities = [AllContactModel::class, FavoriteContactModel::class,
    ScheduleAlarmModel::class], version = 1, exportSchema = false)
abstract class ContactDb: RoomDatabase() {
    abstract fun getContactDao(): ContactDao
}