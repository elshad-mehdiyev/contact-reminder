package com.iremeber.rememberfriends.ui.settingpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.data.models.AllRingtonesModel
import com.iremeber.rememberfriends.data.repo.IRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingPageViewModel @Inject constructor(
    private val repository: IRepository
): ViewModel() {


    private val _requestCodeFromDataStore = MutableLiveData<Int>()
    val requestCodeFromDataStore: LiveData<Int>
        get() = _requestCodeFromDataStore


    private val _ringtonesListData = MutableLiveData<List<AllRingtonesModel>>()
    val ringtonesListData: LiveData<List<AllRingtonesModel>>
        get() = _ringtonesListData


    fun getRingtonesFromDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            val ringtonesListAsync = async { repository.getAllRingtonesFromDevice() }
            val ringtones = ringtonesListAsync.await()
            _ringtonesListData.postValue(ringtones)
        }
    }

    fun saveToDataStore(key: String, value: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveToDataStore(key, value)
        }
    }
    fun getDataFromDataStore(key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDataFromDataStore(key).collect {
                _requestCodeFromDataStore.postValue(it)
            }
        }
    }
}