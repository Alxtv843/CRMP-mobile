package com.crmp.mobile.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "crmp_settings")

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val nickname = stringPreferencesKey("nickname")
        val cacheUrl = stringPreferencesKey("cache_url")
        val dataUrl = stringPreferencesKey("data_url")
        val selectedServerId = stringPreferencesKey("selected_server_id")
    }

    val nickname: Flow<String> = context.dataStore.data.map { it[Keys.nickname] ?: "Player" }
    val cacheUrl: Flow<String> = context.dataStore.data.map {
        it[Keys.cacheUrl] ?: "https://example.com/crmp/cache.zip"
    }
    val dataUrl: Flow<String> = context.dataStore.data.map {
        it[Keys.dataUrl] ?: "https://example.com/crmp/gta_sa_data/"
    }
    val selectedServerId: Flow<String?> = context.dataStore.data.map { it[Keys.selectedServerId] }

    suspend fun setNickname(value: String) {
        context.dataStore.edit { it[Keys.nickname] = value.trim().ifEmpty { "Player" } }
    }

    suspend fun setCacheUrl(value: String) {
        context.dataStore.edit { it[Keys.cacheUrl] = value.trim() }
    }

    suspend fun setDataUrl(value: String) {
        context.dataStore.edit { it[Keys.dataUrl] = value.trim() }
    }

    suspend fun setSelectedServerId(id: String?) {
        context.dataStore.edit {
            if (id == null) it.remove(Keys.selectedServerId) else it[Keys.selectedServerId] = id
        }
    }
}
