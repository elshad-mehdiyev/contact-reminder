package com.iremeber.rememberfriends.domain.interactors

import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel
import com.iremeber.rememberfriends.data.repo.LocalRepository
import javax.inject.Inject

class SaveAllToAllContactModelUseCase @Inject constructor(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(list: ArrayList<AllContactModel>) {
        repository.saveAllToAllContactModel(list)
    }
}