package com.iremeber.rememberfriends.data.repo

import androidx.lifecycle.LiveData
import com.iremeber.rememberfriends.data.local.ContactDao
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import javax.inject.Inject


class ReminderCardRepository @Inject constructor(
    private val dao: ContactDao
) {
    suspend fun saveToFavorites(contact: FavoriteContactModel) {
        dao.saveToFavorites(contact)
    }
    suspend fun deleteFromFavorites(requestCode: Int) {
        dao.deleteFromFavorites(requestCode)
    }
    fun getAllFromFavorites(): LiveData<List<FavoriteContactModel>> {
        return dao.getAllFromFavorites()
    }
    suspend fun updateReminderCard(
        date: String,
        interval: String,
        beginHour: String,
        endHour: String,
        dateMessage: String,
        intervalMessage: String,
        requestCode: Int
    ) {
        dao.updateReminderCard(date, interval, beginHour, endHour,dateMessage, intervalMessage, requestCode)
    }
    suspend fun updateReminderCardAfterAlarmTrigger(date: String, requestCode: Int, dateMessage: String) {
        dao.updateReminderCardAfterAlarmTrigger(date, requestCode, dateMessage)
    }
}