package com.crmp.mobile.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crmp.mobile.viewmodel.AppUiState

@Composable
fun HomeScreen(
    state: AppUiState,
    onLaunch: () -> Unit,
    onOpenServers: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "CRMP Mobile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Лаунчер для CRMP / SA-MP",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ник", style = MaterialTheme.typography.labelMedium)
                Text(state.nickname, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)

                Spacer(Modifier.height(4.dp))
                Text("Выбранный сервер", style = MaterialTheme.typography.labelMedium)
                val server = state.selectedServer
                if (server != null) {
                    Text(server.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        server.endpoint(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text("Не выбран", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        Button(
            onClick = onLaunch,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            enabled = state.selectedServer != null,
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(Modifier.padding(4.dp))
            Text("Играть", style = MaterialTheme.typography.titleMedium)
        }

        OutlinedButton(
            onClick = onOpenServers,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text("Выбрать сервер")
        }

        Spacer(Modifier.weight(1f))
        Text(
            text = "Игровые данные GTA SA не входят в приложение. Укажите свой легальный кэш в настройках.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}
