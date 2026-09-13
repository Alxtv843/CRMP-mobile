package com.crmp.mobile.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crmp.mobile.download.CacheStatus
import com.crmp.mobile.download.DownloadState
import com.crmp.mobile.ui.components.CacheDownloadCard
import com.crmp.mobile.viewmodel.AppUiState

@Composable
fun SettingsScreen(
    state: AppUiState,
    downloadState: DownloadState,
    cacheStatus: CacheStatus,
    onSaveNickname: (String) -> Unit,
    onSaveCacheUrl: (String) -> Unit,
    onSaveDataUrl: (String) -> Unit,
    onDownloadCache: (url: String) -> Unit,
    onCancelDownload: () -> Unit,
) {
    var nick by remember { mutableStateOf(state.nickname) }
    var cache by remember { mutableStateOf(state.cacheUrl) }
    var data by remember { mutableStateOf(state.dataUrl) }

    LaunchedEffect(state.nickname, state.cacheUrl, state.dataUrl) {
        nick = state.nickname
        cache = state.cacheUrl
        data = state.dataUrl
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "Настройки",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        OutlinedTextField(
            value = nick,
            onValueChange = { nick = it },
            label = { Text("Никнейм") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        )
        Button(
            onClick = { onSaveNickname(nick) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Сохранить ник") }

        Spacer(Modifier.height(8.dp))
        Text(
            "Кэш / данные",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "Укажите HTTPS/HTTP URL вашего легального кэша (например cache.zip). " +
                "По умолчанию — плейсхолдеры example.com; разместите файлы на своём хостинге. " +
                "Приложение не распространяет ассеты GTA SA.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedTextField(
            value = cache,
            onValueChange = { cache = it },
            label = { Text("URL кэша (cache.zip)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        )
        Button(
            onClick = { onSaveCacheUrl(cache) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Сохранить URL кэша") }

        CacheDownloadCard(
            downloadState = downloadState,
            cacheStatus = cacheStatus,
            onDownload = {
                onSaveCacheUrl(cache)
                onDownloadCache(cache)
            },
            onCancel = onCancelDownload,
        )

        OutlinedTextField(
            value = data,
            onValueChange = { data = it },
            label = { Text("URL данных GTA SA") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        )
        Button(
            onClick = { onSaveDataUrl(data) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Сохранить URL данных") }
        Text(
            "URL данных сохраняется для будущего использования клиентом; " +
                "кнопка «Скачать кэш» использует URL кэша выше.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
