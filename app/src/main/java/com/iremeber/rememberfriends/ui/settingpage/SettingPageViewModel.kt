package com.iremeber.rememberfriends.ui.settingpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.domain.GetDataFromDataStoreUseCase
import com.iremeber.rememberfriends.domain.SaveToDataStoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingPageViewModel @Inject constructor(
    private val saveToDataStoreUseCase: SaveToDataStoreUseCase,
    private val getDataFromDataStoreUseCase: GetDataFromDataStoreUseCase
): ViewModel() {


    private val _requestCodeFromDataStore = MutableLiveData<Int>()
    val requestCodeFromDataStore: LiveData<Int>
        get() = _requestCodeFromDataStore

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