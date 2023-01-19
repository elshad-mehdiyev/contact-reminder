package com.iremeber.rememberfriends.domain.interactors

import com.iremeber.rememberfriends.data.models.enums.DataSourceType
import com.iremeber.rememberfriends.data.repo.LocalRepository
import javax.inject.Inject

class DeleteDataUseCase @Inject constructor(
    private val repository: LocalRepository
) {
    suspend operator fun invoke(requestCode: Int, source: DataSourceType) {
        repository.deleteData(requestCode, source)
    }
}