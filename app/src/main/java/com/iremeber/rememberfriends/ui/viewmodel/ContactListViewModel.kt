package com.iremeber.rememberfriends.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.data.models.AllContactModel
import com.iremeber.rememberfriends.data.models.AllRingtonesModel
import com.iremeber.rememberfriends.data.models.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.repo.ContactRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    application: Application,
    private val repositoryInterface: ContactRepositoryInterface): AndroidViewModel(application) {


    private val _requestCodeFromDataStore = MutableLiveData<Int>()
    val requestCodeFromDataStore: LiveData<Int>
        get() = _requestCodeFromDataStore


    private val _contactListData = MutableLiveData<List<AllContactModel>>()
    val contactListData: LiveData<List<AllContactModel>>
        get() = _contactListData

    private val _ringtonesListData = MutableLiveData<List<AllRingtonesModel>>()
    val ringtonesListData: LiveData<List<AllRingtonesModel>>
        get() = _ringtonesListData

    val allContactList = repositoryInterface.getAllContact()
    val favoriteContactList = repositoryInterface.getAllFromFavorites()

    fun getRingtonesFromDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val ringtonesListAsync = async { repositoryInterface.getAllRingtonesFromDevice() }
            val ringtones = ringtonesListAsync.await()
            _ringtonesListData.postValue(ringtones)
        }
    }

    fun getContactFromDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val contactsListAsync = async { repositoryInterface.getContactFromDevice() }
            val contacts = contactsListAsync.await()
            _contactListData.postValue(contacts)
        }
    }
    fun saveToAllContactList(list: List<AllContactModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.saveContacts(list)
        }
    }
    fun saveToFavoriteContactList(contactModel: FavoriteContactModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.saveToFavorites(contactModel)
        }
    }
    fun deleteFromFavoriteContactList(requestCode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.deleteFromFavorites(requestCode)
        }
    }
    fun updateFavoriteContactList(date: String,
                                  interval: String,
                                  beginHour: String,
                                  endHour: String,
                                  dateMessage: String,
                                  intervalMessage: String,
                                  requestCode: Int,
                                  updateRequestCode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.updateReminderCard(date, interval, beginHour, endHour,
                dateMessage, intervalMessage, requestCode, updateRequestCode)
        }
    }
    fun saveToScheduleAlarmModel(scheduleAlarmModel: ScheduleAlarmModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.saveToScheduleAlarm(scheduleAlarmModel)
        }
    }
    fun deleteFromScheduleAlarmModel(requestCode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.deleteFromScheduleAlarmModel(requestCode)
        }
    }
    fun updateScheduleAlarmModel(newTimeInMillis: Long, requestCode: Int, interval: Int, updateRequestCode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.updateScheduleAlarm(newTimeInMillis, requestCode, interval, updateRequestCode)
        }
    }
    fun saveToDataStore(key: String, value: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.saveToDataStore(key, value)
        }
    }
    fun getDataFromDataStore(key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.getDataFromDataStore(key).collect {
                _requestCodeFromDataStore.postValue(it)
            }
        }
    }
}