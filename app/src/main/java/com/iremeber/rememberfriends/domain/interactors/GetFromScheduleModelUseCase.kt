package com.iremeber.rememberfriends.domain.interactors

import com.iremeber.rememberfriends.data.repo.LocalRepository
import javax.inject.Inject

class GetFromScheduleModelUseCase @Inject constructor(
    private val repository: LocalRepository
) {
    operator fun invoke() = repository.getAllFromScheduleAlarmModel()
}