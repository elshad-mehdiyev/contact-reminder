package com.iremeber.rememberfriends.di

import android.content.Context
import androidx.room.Room
import com.iremeber.rememberfriends.data.local.AllContacts
import com.iremeber.rememberfriends.data.local.AllRingtones
import com.iremeber.rememberfriends.data.local.ContactDao
import com.iremeber.rememberfriends.data.local.ContactDb
import com.iremeber.rememberfriends.data.repo.ContactRepository
import com.iremeber.rememberfriends.data.repo.ContactRepositoryInterface
import com.iremeber.rememberfriends.utils.util.Constants.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        ContactDb::class.java,
        DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideDao(
        database: ContactDb
    ) = database.getContactDao()

    @Singleton
    @Provides
    fun provideRepository(
        dao: ContactDao,
        allContacts: AllContacts,
        @ApplicationContext context: Context,
        allRingtones: AllRingtones
    ) = ContactRepository(dao, allContacts, context, allRingtones) as ContactRepositoryInterface

    @Singleton
    @Provides
    fun provideContext(
        @ApplicationContext context: Context
    ) = context
}