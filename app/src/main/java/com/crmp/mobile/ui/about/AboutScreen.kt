package com.crmp.mobile.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "О приложении",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "CRMP Mobile — открытый каркас лаунчера и клиента для серверов CRMP / SA-MP.",
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            "Версия 0.1.4",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "Юридическая информация",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "Приложение не содержит ассетов GTA: San Andreas, OBB, взломанных APK " +
                "или проприетарных бинарников сторонних клиентов (в т.ч. Black Russia). " +
                "Вы должны самостоятельно обеспечить легальные игровые данные. " +
                "Rockstar Games и GTA являются товарными знаками соответствующих правообладателей.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "Лицензия: MIT\nРепозиторий: github.com/Alxtv843/CRMP-mobile",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
