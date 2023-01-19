package com.iremeber.rememberfriends.domain.interactors

import com.iremeber.rememberfriends.data.models.enums.DataSourceType
import com.iremeber.rememberfriends.data.models.db_entities.FavoriteContactModel
import com.iremeber.rememberfriends.data.models.db_entities.ScheduleAlarmModel
import com.iremeber.rememberfriends.data.repo.LocalRepository
import javax.inject.Inject

class SaveDataUseCase @Inject constructor(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(contact: FavoriteContactModel? = null,
                                scheduleAlarmModel: ScheduleAlarmModel? = null,
                                source: DataSourceType
    ) {
        repository.saveData(contact, scheduleAlarmModel, source)
    }
}