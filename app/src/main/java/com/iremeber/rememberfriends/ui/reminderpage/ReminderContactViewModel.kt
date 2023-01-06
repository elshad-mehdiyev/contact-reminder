package com.iremeber.rememberfriends.ui.reminderpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.repo.ReminderCardRepository
import com.iremeber.rememberfriends.data.repo.ScheduleReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderContactViewModel @Inject constructor(
    private val reminderCardRepository: ReminderCardRepository,
    private val scheduleReminderRepository: ScheduleReminderRepository
): ViewModel() {

    val favoriteContactList = reminderCardRepository.getAllFromFavorites()

    fun deleteFromFavoriteContactList(requestCode: Int) {
        viewModelScope.launch {
            reminderCardRepository.deleteFromFavorites(requestCode)
        }
    }
    fun updateFavoriteContactList(date: String,
                                  interval: String,
                                  beginHour: String,
                                  endHour: String,
                                  dateMessage: String,
                                  intervalMessage: String,
                                  requestCode: Int) {
        viewModelScope.launch {
            reminderCardRepository.updateReminderCard(date, interval, beginHour, endHour,
                dateMessage, intervalMessage, requestCode)
        }
    }
    fun deleteFromScheduleAlarmModel(requestCode: Int) {
        viewModelScope.launch {
            scheduleReminderRepository.deleteFromScheduleAlarmModel(requestCode)
        }
    }
    fun updateScheduleAlarmModel(newTimeInMillis: Long, requestCode: Int, interval: Int) {
        viewModelScope.launch {
            scheduleReminderRepository.updateScheduleAlarm(newTimeInMillis, requestCode, interval )
        }
    }
    fun saveToFavoriteContactList(contactModel: FavoriteContactModel) {
        viewModelScope.launch {
            reminderCardRepository.saveToFavorites(contactModel)
        }
    }

    fun saveToScheduleAlarmModel(scheduleAlarmModel: ScheduleAlarmModel) {
        viewModelScope.launch {
            scheduleReminderRepository.saveToScheduleAlarm(scheduleAlarmModel)
        }
    }
}