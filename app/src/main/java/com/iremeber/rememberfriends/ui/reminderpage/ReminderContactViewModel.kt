package com.iremeber.rememberfriends.ui.reminderpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.data.models.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.repo.IRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderContactViewModel @Inject constructor(
    private val repository: IRepository
): ViewModel() {

    val favoriteContactList = repository.getAllFromFavorites()

    fun deleteFromFavoriteContactList(requestCode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFromFavorites(requestCode)
        }
    }
    fun updateFavoriteContactList(date: String,
                                  interval: String,
                                  beginHour: String,
                                  endHour: String,
                                  dateMessage: String,
                                  intervalMessage: String,
                                  requestCode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReminderCard(date, interval, beginHour, endHour,
                dateMessage, intervalMessage, requestCode)
        }
    }
    fun deleteFromScheduleAlarmModel(requestCode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFromScheduleAlarmModel(requestCode)
        }
    }
    fun updateScheduleAlarmModel(newTimeInMillis: Long, requestCode: Int, interval: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateScheduleAlarm(newTimeInMillis, requestCode, interval )
        }
    }
    fun saveToFavoriteContactList(contactModel: FavoriteContactModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveToFavorites(contactModel)
        }
    }

    fun saveToScheduleAlarmModel(scheduleAlarmModel: ScheduleAlarmModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveToScheduleAlarm(scheduleAlarmModel)
        }
    }
}