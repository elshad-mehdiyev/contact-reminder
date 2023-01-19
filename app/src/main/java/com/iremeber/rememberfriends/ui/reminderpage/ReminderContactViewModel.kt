package com.iremeber.rememberfriends.ui.reminderpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iremeber.rememberfriends.data.models.enums.DataSourceType
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.models.enums.UpdateSourceType
import com.iremeber.rememberfriends.domain.update_entities.UpdateReminderCardAfterAlarmTriggerModel
import com.iremeber.rememberfriends.domain.update_entities.UpdateReminderCardModel
import com.iremeber.rememberfriends.domain.update_entities.UpdateScheduleAlarmModel
import com.iremeber.rememberfriends.domain.interactors.DeleteDataUseCase
import com.iremeber.rememberfriends.domain.interactors.GetFromFavoriteModelUseCase
import com.iremeber.rememberfriends.domain.interactors.SaveDataUseCase
import com.iremeber.rememberfriends.domain.interactors.UpdateDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderContactViewModel @Inject constructor(
    private val saveDataUseCase: SaveDataUseCase,
    private val deleteDataUseCase: DeleteDataUseCase,
    private val updateDataUseCase: UpdateDataUseCase,
    private val getFromFavoriteModelUseCase: GetFromFavoriteModelUseCase
): ViewModel() {

    val favoriteContactList = getFromFavoriteModelUseCase()

    fun deleteData(requestCode: Int, source: DataSourceType) {
        viewModelScope.launch {
            deleteDataUseCase(requestCode, source)
        }
    }
    fun updateData(reminderCard: UpdateReminderCardModel? = null,
                   ReminderAfterAlarm: UpdateReminderCardAfterAlarmTriggerModel? = null,
                   updateScheduleAlarmModel: UpdateScheduleAlarmModel? = null,
                   updateDataSourceType: UpdateSourceType
    ) {
        viewModelScope.launch {
            updateDataUseCase(reminderCard, ReminderAfterAlarm, updateScheduleAlarmModel, updateDataSourceType)
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
}