package com.iremeber.rememberfriends.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel

@Database(entities = [FavoriteContactModel::class, ScheduleAlarmModel::class, AllContactModel::class],
    version = 1, exportSchema = false)
abstract class ContactDb: RoomDatabase() {
    abstract fun getContactDao(): ContactDao
    abstract fun getScheduleDao(): ScheduleDao
    abstract fun getContactsFromDeviceDao(): ContactsFromDeviceDao
}