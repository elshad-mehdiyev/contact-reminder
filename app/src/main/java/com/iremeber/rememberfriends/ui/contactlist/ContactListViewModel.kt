package com.iremeber.rememberfriends.ui.contactlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.data.models.enums.DataSourceType
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.models.device_entities.AllContactModel
import com.iremeber.rememberfriends.domain.interactors.GetContactFromDeviceUseCase
import com.iremeber.rememberfriends.domain.interactors.GetDataFromDataStoreUseCase
import com.iremeber.rememberfriends.domain.interactors.SaveDataUseCase
import com.iremeber.rememberfriends.domain.interactors.SaveToDataStoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val getContactFromDeviceUseCase: GetContactFromDeviceUseCase,
    private val saveDataUseCase: SaveDataUseCase,
    private val saveToDataStoreUseCase: SaveToDataStoreUseCase,
    private val getDataFromDataStoreUseCase: GetDataFromDataStoreUseCase
    ): ViewModel() {

    private val _requestCodeFromDataStore = MutableLiveData<Int>()
    val requestCodeFromDataStore: LiveData<Int>
        get() = _requestCodeFromDataStore


    private val _contactListData = MutableLiveData<List<AllContactModel>>()
    val contactListData: LiveData<List<AllContactModel>>
        get() = _contactListData


    fun getContactFromDevice() {
        viewModelScope.launch {
            val contactsListAsync = async { getContactFromDeviceUseCase() }
            val contacts = contactsListAsync.await()
            _contactListData.value = contacts
        }
    }
    fun saveDataToDb(contact: FavoriteContactModel? = null,
                     scheduleAlarmModel: ScheduleAlarmModel? = null,
                     source: DataSourceType
    ) {
        viewModelScope.launch {
            saveDataUseCase(contact, scheduleAlarmModel, source)
        }
    }

    fun saveToDataStore(key: String, value: Int) {
        viewModelScope.launch {
            saveToDataStoreUseCase(key, value)
        }
    }
    fun getDataFromDataStore(key: String) {
        viewModelScope.launch {
            getDataFromDataStoreUseCase(key).collect {
                _requestCodeFromDataStore.value = it
            }
        }
    }
}