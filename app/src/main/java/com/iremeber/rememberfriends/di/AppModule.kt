package com.iremeber.rememberfriends.di

import android.content.Context
import androidx.room.Room
import com.iremeber.rememberfriends.data.local.AllContacts
import com.iremeber.rememberfriends.data.local.ContactDao
import com.iremeber.rememberfriends.data.local.ContactDb
import com.iremeber.rememberfriends.data.local.ScheduleDao
import com.iremeber.rememberfriends.data.repo.*
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
    fun provideContactDao(
        database: ContactDb
    ) = database.getContactDao()

    @Singleton
    @Provides
    fun provideScheduleDao(
        database: ContactDb
    ) = database.getScheduleDao()

    @Singleton
    @Provides
    fun provideReminderCardRepository(
        contactDao: ContactDao,
        scheduleDao: ScheduleDao
    ) = LocalRepository(contactDao, scheduleDao)

    @Singleton
    @Provides
    fun provideDeviceRepository(
        allContacts: AllContacts
    ) = DeviceRepository(allContacts)

    @Singleton
    @Provides
    fun providePreferenceRepository(
        @ApplicationContext context: Context,
    ) = PreferenceRepository(context)

    @Singleton
    @Provides
    fun provideContext(
        @ApplicationContext context: Context
    ) = context
}