package com.crmp.mobile.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crmp.mobile.client.NativeClient
import com.crmp.mobile.data.PreferencesRepository
import com.crmp.mobile.data.ServerRepository
import com.crmp.mobile.model.Server
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppUiState(
    val nickname: String = "Player",
    val cacheUrl: String = "",
    val dataUrl: String = "",
    val servers: List<Server> = emptyList(),
    val selectedServerId: String? = null,
    val lastLaunchMessage: String? = null,
) {
    val selectedServer: Server?
        get() = servers.firstOrNull { it.id == selectedServerId } ?: servers.firstOrNull()

    val favorites: List<Server> get() = servers.filter { it.isFavorite }
}

/**
 * Plain [ViewModel] — Application/Context injected via [AppViewModelFactory].
 * Avoids AndroidViewModel + enableEdgeToEdge OEM crash paths on MIUI.
 */
class AppViewModel(appContext: Context) : ViewModel() {

    private val appContext = appContext.applicationContext
    private val prefs = PreferencesRepository(this.appContext)
    private val serversRepo = ServerRepository(this.appContext)

    val uiState: StateFlow<AppUiState> = combine(
        prefs.nickname,
        prefs.cacheUrl,
        prefs.dataUrl,
        prefs.selectedServerId,
        serversRepo.servers,
    ) { nick, cache, data, selectedId, servers ->
        AppUiState(
            nickname = nick,
            cacheUrl = cache,
            dataUrl = data,
            servers = servers,
            selectedServerId = selectedId,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppUiState())

    fun setNickname(value: String) = viewModelScope.launch { prefs.setNickname(value) }
    fun setCacheUrl(value: String) = viewModelScope.launch { prefs.setCacheUrl(value) }
    fun setDataUrl(value: String) = viewModelScope.launch { prefs.setDataUrl(value) }
    fun selectServer(id: String) = viewModelScope.launch { prefs.setSelectedServerId(id) }

    fun addServer(name: String, host: String, port: Int) = viewModelScope.launch {
        serversRepo.upsert(
            Server(
                name = name.ifBlank { "$host:$port" },
                host = host.trim(),
                port = port.coerceIn(1, 65535),
            )
        )
    }

    fun removeServer(id: String) = viewModelScope.launch { serversRepo.remove(id) }
    fun toggleFavorite(id: String) = viewModelScope.launch { serversRepo.toggleFavorite(id) }

    fun launchGame() {
        val state = uiState.value
        val server = state.selectedServer
        if (server == null) {
            toast("Сначала выберите сервер")
            return
        }
        // Native stub — real game loop TBD
        val code = NativeClient.launch(server.endpoint(), state.nickname)
        val msg = when (code) {
            NativeClient.RESULT_OK -> "Клиент: запуск запрошен (${server.endpoint()})"
            NativeClient.RESULT_NOT_IMPLEMENTED ->
                "Клиент-заглушка: NativeClient ещё не реализован (код $code)"
            else -> "Клиент вернул код $code"
        }
        toast(msg)
    }

    private fun toast(text: String) {
        Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
    }
}
