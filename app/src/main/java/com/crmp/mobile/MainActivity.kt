package com.crmp.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.crmp.mobile.ui.about.AboutScreen
import com.crmp.mobile.ui.home.HomeScreen
import com.crmp.mobile.ui.navigation.Dest
import com.crmp.mobile.ui.servers.ServersScreen
import com.crmp.mobile.ui.settings.SettingsScreen
import com.crmp.mobile.ui.theme.CRMPTheme
import com.crmp.mobile.viewmodel.AppViewModel
import com.crmp.mobile.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Do NOT call enableEdgeToEdge() — known OEM (Xiaomi/MIUI) cold-start crashes.
        setContent {
            CRMPTheme {
                CrMpApp(
                    vm = viewModel(
                        factory = AppViewModelFactory(application),
                    ),
                )
            }
        }
    }
}

@Composable
private fun CrMpApp(vm: AppViewModel) {
    // collectAsState for max OEM compatibility (avoids lifecycle-runtime-compose edge cases).
    val state by vm.uiState.collectAsState()
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route ?: Dest.Home.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                Dest.bottom.forEach { dest ->
                    NavigationBarItem(
                        selected = current == dest.route,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(Dest.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(iconFor(dest), contentDescription = dest.label) },
                        label = { Text(dest.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Dest.Home.route) {
                HomeScreen(
                    state = state,
                    onLaunch = vm::launchGame,
                    onOpenServers = { navController.navigate(Dest.Servers.route) },
                )
            }
            composable(Dest.Servers.route) {
                ServersScreen(
                    state = state,
                    onSelect = vm::selectServer,
                    onToggleFavorite = vm::toggleFavorite,
                    onRemove = vm::removeServer,
                    onAdd = vm::addServer,
                )
            }
            composable(Dest.Settings.route) {
                SettingsScreen(
                    state = state,
                    onSaveNickname = vm::setNickname,
                    onSaveCacheUrl = vm::setCacheUrl,
                    onSaveDataUrl = vm::setDataUrl,
                )
            }
            composable(Dest.About.route) {
                AboutScreen()
            }
        }
    }
}

/** Only Icons.Filled.* from material-icons-core — avoid List / AutoMirrored (NoSuchFieldError on OEM). */
private fun iconFor(dest: Dest): ImageVector = when (dest) {
    Dest.Home -> Icons.Filled.Home
    Dest.Servers -> Icons.Filled.Star
    Dest.Settings -> Icons.Filled.Settings
    Dest.About -> Icons.Filled.Info
}
