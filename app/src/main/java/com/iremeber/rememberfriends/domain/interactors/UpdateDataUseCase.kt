package com.iremeber.rememberfriends.domain.interactors

import com.iremeber.rememberfriends.data.models.enums.UpdateSourceType
import com.iremeber.rememberfriends.domain.update_entities.UpdateReminderCardAfterAlarmTriggerModel
import com.iremeber.rememberfriends.domain.update_entities.UpdateReminderCardModel
import com.iremeber.rememberfriends.domain.update_entities.UpdateScheduleAlarmModel
import com.iremeber.rememberfriends.data.repo.LocalRepository
import javax.inject.Inject

class UpdateDataUseCase @Inject constructor(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(reminderCard: UpdateReminderCardModel? = null,
                                reminderAfterAlarm: UpdateReminderCardAfterAlarmTriggerModel? = null,
                                updateScheduleAlarmModel: UpdateScheduleAlarmModel? = null,
                                updateDataSourceType: UpdateSourceType) {
        repository.updateData(reminderCard, reminderAfterAlarm, updateScheduleAlarmModel, updateDataSourceType)
    }
}