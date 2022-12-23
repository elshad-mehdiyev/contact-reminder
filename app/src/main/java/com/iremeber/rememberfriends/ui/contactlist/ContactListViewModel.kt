package com.iremeber.rememberfriends.ui.contactlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.data.models.AllContactModel
import com.iremeber.rememberfriends.data.models.AllRingtonesModel
import com.iremeber.rememberfriends.data.models.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.repo.IRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val repositoryInterface: IRepository
    ): ViewModel() {

    private val _requestCodeFromDataStore = MutableLiveData<Int>()
    val requestCodeFromDataStore: LiveData<Int>
        get() = _requestCodeFromDataStore


    private val _contactListData = MutableLiveData<List<AllContactModel>>()
    val contactListData: LiveData<List<AllContactModel>>
        get() = _contactListData


    fun getContactFromDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val contactsListAsync = async { repositoryInterface.getContactFromDevice() }
            val contacts = contactsListAsync.await()
            _contactListData.postValue(contacts)
        }
    }
    fun saveToFavoriteContactList(contactModel: FavoriteContactModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.saveToFavorites(contactModel)
        }
    }

    fun saveToScheduleAlarmModel(scheduleAlarmModel: ScheduleAlarmModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.saveToScheduleAlarm(scheduleAlarmModel)
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