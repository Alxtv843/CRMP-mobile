package com.crmp.mobile.ui.navigation

/**
 * Navigation destinations. Enum avoids sealed-class + companion listOf(...)
 * initialization-order NPEs (Dest entries were null on first composition).
 */
enum class Dest(val route: String, val label: String) {
    Home("home", "Главная"),
    Servers("servers", "Серверы"),
    Settings("settings", "Настройки"),
    About("about", "О нас");

    companion object {
        val bottom: List<Dest> = entries
    }
}
