package com.iremeber.rememberfriends.domain.interactors

import com.iremeber.rememberfriends.data.repo.LocalRepository
import javax.inject.Inject

class GetFromFavoriteModelUseCase @Inject constructor(
    private val repository: LocalRepository
) {
    operator fun invoke() = repository.getAllFromFavorites()
}