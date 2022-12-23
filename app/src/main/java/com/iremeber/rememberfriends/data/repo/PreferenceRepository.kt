package com.iremeber.rememberfriends.data.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.iremeber.rememberfriends.utils.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.SETTING_PREFERENCE)

class PreferenceRepository @Inject constructor(
    private val context: Context,
    ): IRepository {
    override suspend fun saveToDataStore(key: String, value: Int) {
        context.dataStore.edit {
            val preferenceKey = intPreferencesKey(key)
            it[preferenceKey] = value
        }
    }

    override suspend fun getDataFromDataStore(key: String): Flow<Int> =
        context.dataStore.data.map { dataStore ->
            val preferenceKey = intPreferencesKey(key)
            dataStore[preferenceKey] ?: 0
        }
}