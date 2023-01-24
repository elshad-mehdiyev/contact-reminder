package com.iremeber.rememberfriends.ui.contactlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.data.models.enums.DataSourceType
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.models.device_entities.AllContactModel
import com.iremeber.rememberfriends.domain.interactors.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val getAllContactFromDbUseCase: GetAllContactFromDbUseCase,
    private val saveDataUseCase: SaveDataUseCase,
    private val saveToDataStoreUseCase: SaveToDataStoreUseCase,
    private val getDataFromDataStoreUseCase: GetDataFromDataStoreUseCase
    ): ViewModel() {

    private val _requestCodeFromDataStore = MutableLiveData<Int>()
    val requestCodeFromDataStore: LiveData<Int>
        get() = _requestCodeFromDataStore


    val contactListData = getAllContactFromDbUseCase()


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