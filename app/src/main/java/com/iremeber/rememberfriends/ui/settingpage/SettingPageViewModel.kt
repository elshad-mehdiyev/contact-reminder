package com.iremeber.rememberfriends.ui.settingpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.data.models.device_entities.AllRingtonesModel
import com.iremeber.rememberfriends.data.repo.DeviceRepository
import com.iremeber.rememberfriends.data.repo.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingPageViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val deviceRepository: DeviceRepository
): ViewModel() {


    private val _requestCodeFromDataStore = MutableLiveData<Int>()
    val requestCodeFromDataStore: LiveData<Int>
        get() = _requestCodeFromDataStore


    private val _ringtonesListData = MutableLiveData<List<AllRingtonesModel>>()
    val ringtonesListData: LiveData<List<AllRingtonesModel>>
        get() = _ringtonesListData


    fun getRingtonesFromDevice() {
        viewModelScope.launch{
            val ringtonesListAsync = async { deviceRepository.getAllRingtonesFromDevice() }
            val ringtones = ringtonesListAsync.await()
            _ringtonesListData.postValue(ringtones)
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