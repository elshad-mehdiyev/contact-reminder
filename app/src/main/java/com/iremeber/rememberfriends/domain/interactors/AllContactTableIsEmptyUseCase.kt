package com.iremeber.rememberfriends.domain.interactors

import com.iremeber.rememberfriends.data.repo.LocalRepository
import javax.inject.Inject

class AllContactTableIsEmptyUseCase @Inject constructor(
    private val repository: LocalRepository
) {
    suspend operator fun invoke() = repository.allContactModelTableIsEmpty()
}