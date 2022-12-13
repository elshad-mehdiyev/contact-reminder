package com.iremeber.rememberfriends.di

import com.iremeber.rememberfriends.data.repo.ContactRepositoryInterface
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepositoryEntryPoints {
    var repository: ContactRepositoryInterface
}