package com.iremeber.rememberfriends.ui.main_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.domain.interactors.AllContactTableIsEmptyUseCase
import com.iremeber.rememberfriends.domain.interactors.DeleteAllFromAllContactModelUseCase
import com.iremeber.rememberfriends.domain.interactors.GetContactFromDeviceUseCase
import com.iremeber.rememberfriends.domain.interactors.SaveAllToAllContactModelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val saveAllToAllContactModelUseCase: SaveAllToAllContactModelUseCase,
    private val deleteAllFromAllContactModelUseCase: DeleteAllFromAllContactModelUseCase,
    private val getContactFromDeviceUseCase: GetContactFromDeviceUseCase,
    private val allContactTableIsEmptyUseCase: AllContactTableIsEmptyUseCase
): ViewModel() {
    fun save() {
        viewModelScope.launch {
            val list = getContactFromDeviceUseCase()
            if (allContactTableIsEmptyUseCase() > 0) {
                deleteAllFromAllContactModelUseCase()
            }
            saveAllToAllContactModelUseCase(list)
        }
    }
}