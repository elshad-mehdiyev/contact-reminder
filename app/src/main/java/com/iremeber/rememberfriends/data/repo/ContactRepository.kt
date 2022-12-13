package com.iremeber.rememberfriends.data.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import com.iremeber.rememberfriends.data.local.AllContacts
import com.iremeber.rememberfriends.data.local.ContactDao
import com.iremeber.rememberfriends.data.models.AllContactModel
import com.iremeber.rememberfriends.data.models.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.ScheduleAlarmModel
import com.iremeber.rememberfriends.utils.util.Constants.SETTING_PREFERENCE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTING_PREFERENCE)

class ContactRepository @Inject constructor(
    private val dao: ContactDao,
    private val fetchContact: AllContacts,
    private val context: Context
): ContactRepositoryInterface {
    override suspend fun saveContacts(list: List<AllContactModel>) {
        dao.saveContacts(list)
    }
    override fun getAllContact(): LiveData<List<AllContactModel>> {
        return dao.getAllContacts()
    }

    override suspend fun getContactFromDevice(): ArrayList<AllContactModel> {
        return fetchContact.getPhoneContacts()
    }

    /**
     * FavoriteContact  Table
     */
    override suspend fun saveToFavorites(contact: FavoriteContactModel) {
        dao.saveToFavorites(contact)
    }

    override suspend fun deleteFromFavorites(requestCode: Int) {
        dao.deleteFromFavorites(requestCode)
    }

    override fun getAllFromFavorites(): LiveData<List<FavoriteContactModel>> {
        return dao.getAllFromFavorites()
    }

    override suspend fun updateReminderCard(
        date: String,
        interval: String,
        beginHour: String,
        endHour: String,
        dateMessage: String,
        intervalMessage: String,
        requestCode: Int,
        updateRequestCode: Int
    ) {
        dao.updateReminderCard(date, interval, beginHour, endHour,dateMessage, intervalMessage, requestCode, updateRequestCode)
    }

    override suspend fun updateReminderCardAfterAlarmTrigger(date: String, requestCode: Int, dateMessage: String) {
        dao.updateReminderCardAfterAlarmTrigger(date, requestCode, dateMessage)
    }

    /**
     * ScheduleAlarmModel
     */
    override suspend fun saveToScheduleAlarm(scheduleAlarmModel: ScheduleAlarmModel) {
        dao.saveToScheduleAlarm(scheduleAlarmModel)
    }

    override suspend fun deleteFromScheduleAlarmModel(requestCode: Int) {
        dao.deleteFromScheduleAlarmModel(requestCode)
    }

    override fun getAllFromScheduleAlarmModel(): List<ScheduleAlarmModel> {
        return dao.getAllFromScheduleAlarmModel()
    }

    override suspend fun updateScheduleAlarm(newTimeInMillis: Long, requestCode: Int, interval: Int, updateRequestCode: Int) {
        dao.updateScheduleAlarm(newTimeInMillis, requestCode, interval, updateRequestCode)
    }

    override suspend fun updateScheduleAlarmAfterAlarmTrigger(
        newTimeInMillis: Long,
        requestCode: Int,
        interval: Int
    ) {
        dao.updateScheduleAlarmAfterAlarmTrigger(newTimeInMillis, requestCode, interval)
    }

    /**
     * DataStore
     */
    override suspend fun saveToDataStore(key: String, value: Int) {
        context.dataStore.edit {
            val preferenceKey = intPreferencesKey(key)
            it[preferenceKey] = value
        }
    }

    override suspend fun getDataFromDataStore(key: String): Flow<Int> =
        context.dataStore.data.map { dataStore ->
            val preferenceKey = intPreferencesKey(key)
            dataStore[preferenceKey] ?: 0
        }
}