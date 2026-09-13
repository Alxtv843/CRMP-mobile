package com.crmp.mobile.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.crmp.mobile.model.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.serverStore: DataStore<Preferences> by preferencesDataStore(name = "crmp_servers")

class ServerRepository(private val context: Context) {

    private val serversKey = stringPreferencesKey("servers_json")

    val servers: Flow<List<Server>> = context.serverStore.data.map { prefs ->
        val raw = prefs[serversKey]
        if (raw.isNullOrBlank()) {
            Server.samples()
        } else {
            decode(raw)
        }
    }

    suspend fun saveAll(list: List<Server>) {
        context.serverStore.edit { it[serversKey] = encode(list) }
    }

    suspend fun upsert(server: Server) {
        context.serverStore.edit { prefs ->
            val current = prefs[serversKey]?.let { decode(it) }?.toMutableList()
                ?: Server.samples().toMutableList()
            val idx = current.indexOfFirst { it.id == server.id }
            if (idx >= 0) current[idx] = server else current.add(server)
            prefs[serversKey] = encode(current)
        }
    }

    suspend fun remove(id: String) {
        context.serverStore.edit { prefs ->
            val current = prefs[serversKey]?.let { decode(it) }?.toMutableList()
                ?: Server.samples().toMutableList()
            current.removeAll { it.id == id }
            prefs[serversKey] = encode(current)
        }
    }

    suspend fun toggleFavorite(id: String) {
        context.serverStore.edit { prefs ->
            val current = prefs[serversKey]?.let { decode(it) }?.toMutableList()
                ?: Server.samples().toMutableList()
            val idx = current.indexOfFirst { it.id == id }
            if (idx >= 0) {
                val s = current[idx]
                current[idx] = s.copy(isFavorite = !s.isFavorite)
                prefs[serversKey] = encode(current)
            }
        }
    }

    private fun encode(list: List<Server>): String {
        val arr = JSONArray()
        list.forEach { s ->
            arr.put(
                JSONObject()
                    .put("id", s.id)
                    .put("name", s.name)
                    .put("host", s.host)
                    .put("port", s.port)
                    .put("isFavorite", s.isFavorite)
            )
        }
        return arr.toString()
    }

    private fun decode(raw: String): List<Server> {
        val arr = JSONArray(raw)
        return buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(
                    Server(
                        id = o.getString("id"),
                        name = o.getString("name"),
                        host = o.getString("host"),
                        port = o.optInt("port", 7777),
                        isFavorite = o.optBoolean("isFavorite", false),
                    )
                )
            }
        }
    }
}
