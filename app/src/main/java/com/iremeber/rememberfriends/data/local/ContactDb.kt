package com.iremeber.rememberfriends.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel

@Database(entities = [FavoriteContactModel::class, ScheduleAlarmModel::class],
    version = 1, exportSchema = false)
abstract class ContactDb: RoomDatabase() {
    abstract fun getContactDao(): ContactDao
    abstract fun getScheduleDao(): ScheduleDao
}