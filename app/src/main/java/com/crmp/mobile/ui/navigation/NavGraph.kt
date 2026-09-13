package com.crmp.mobile.ui.navigation

sealed class Dest(val route: String, val label: String) {
    data object Home : Dest("home", "Главная")
    data object Servers : Dest("servers", "Серверы")
    data object Settings : Dest("settings", "Настройки")
    data object About : Dest("about", "О нас")

    companion object {
        val bottom = listOf(Home, Servers, Settings, About)
    }
}
