package com.iremeber.rememberfriends.domain.interactors

import com.iremeber.rememberfriends.data.repo.PreferenceRepository
import javax.inject.Inject

class GetDataFromDataStoreUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    operator fun invoke(key: String) = preferenceRepository.getDataFromDataStore(key)
}