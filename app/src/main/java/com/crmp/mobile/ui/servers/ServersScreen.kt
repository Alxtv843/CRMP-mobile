package com.crmp.mobile.ui.servers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.crmp.mobile.model.Server
import com.crmp.mobile.viewmodel.AppUiState

@Composable
fun ServersScreen(
    state: AppUiState,
    onSelect: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRemove: (String) -> Unit,
    onAdd: (name: String, host: String, port: Int) -> Unit,
) {
    var showFavoritesOnly by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val visible = if (showFavoritesOnly) state.favorites else state.servers

    // Box + overlay FAB instead of nested Scaffold (outer MainActivity already has one).
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            Text(
                "Серверы",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !showFavoritesOnly,
                    onClick = { showFavoritesOnly = false },
                    label = { Text("Все") },
                )
                FilterChip(
                    selected = showFavoritesOnly,
                    onClick = { showFavoritesOnly = true },
                    label = { Text("Избранное") },
                )
            }
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(visible, key = { it.id }) { server ->
                    ServerCard(
                        server = server,
                        selected = server.id == state.selectedServer?.id,
                        onSelect = { onSelect(server.id) },
                        onToggleFavorite = { onToggleFavorite(server.id) },
                        onRemove = { onRemove(server.id) },
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Добавить сервер")
        }
    }

    if (showAddDialog) {
        AddServerDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, host, port ->
                onAdd(name, host, port)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun ServerCard(
    server: Server,
    selected: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(server.name, fontWeight = FontWeight.SemiBold)
                Text(
                    server.endpoint(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (selected) {
                    Text(
                        "Выбран",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Избранное",
                    tint = if (server.isFavorite) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Удалить")
            }
        }
    }
}

@Composable
private fun AddServerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, host: String, port: Int) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var portText by remember { mutableStateOf("7777") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить сервер") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text("IP / хост") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = portText,
                    onValueChange = { portText = it.filter { c -> c.isDigit() }.take(5) },
                    label = { Text("Порт") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val port = portText.toIntOrNull() ?: 7777
                    if (host.isNotBlank()) onConfirm(name, host, port)
                },
                enabled = host.isNotBlank(),
            ) { Text("Добавить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        },
    )
}
