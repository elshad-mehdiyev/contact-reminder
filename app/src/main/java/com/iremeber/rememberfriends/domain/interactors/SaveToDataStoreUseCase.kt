package com.iremeber.rememberfriends.domain.interactors

import com.iremeber.rememberfriends.data.repo.PreferenceRepository
import javax.inject.Inject

class SaveToDataStoreUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
){
    suspend operator fun invoke(key: String, value: Int) {
        preferenceRepository.saveToDataStore(key, value)
    }
}