package com.iremeber.rememberfriends.ui.contactlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.data.models.device_entities.AllContactModel
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.repo.DeviceRepository
import com.iremeber.rememberfriends.data.repo.PreferenceRepository
import com.iremeber.rememberfriends.data.repo.ReminderCardRepository
import com.iremeber.rememberfriends.data.repo.ScheduleReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val preferenceRepository: PreferenceRepository,
    private val reminderCardRepository: ReminderCardRepository,
    private val scheduleReminderRepository: ScheduleReminderRepository
    ): ViewModel() {

    private val _requestCodeFromDataStore = MutableLiveData<Int>()
    val requestCodeFromDataStore: LiveData<Int>
        get() = _requestCodeFromDataStore


    private val _contactListData = MutableLiveData<List<AllContactModel>>()
    val contactListData: LiveData<List<AllContactModel>>
        get() = _contactListData


    fun getContactFromDevice() {
        viewModelScope.launch {
            val contactsListAsync = async { deviceRepository.getContactFromDevice() }
            val contacts = contactsListAsync.await()
            _contactListData.value = contacts
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

    fun saveToDataStore(key: String, value: Int) {
        viewModelScope.launch {
            preferenceRepository.saveToDataStore(key, value)
        }
    }
    fun getDataFromDataStore(key: String) {
        viewModelScope.launch {
            preferenceRepository.getDataFromDataStore(key).collect {
                _requestCodeFromDataStore.value = it
            }
        }
    }
}