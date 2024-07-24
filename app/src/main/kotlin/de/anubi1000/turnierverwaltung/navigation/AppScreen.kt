package de.anubi1000.turnierverwaltung.navigation

import cafe.adriel.voyager.core.platform.multiplatformName
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey

fun AppScreen.screenKey(vararg args: Any?) = buildString {
    append(this::class.multiplatformName)
    args.forEach { arg ->
        append(arg.toString())
    }
}

interface AppScreen : Screen {
    val navigationMenuType: NavigationMenuType?
    val navigationListType: NavigationListType?

    override val key: ScreenKey
}

enum class NavigationMenuType {
    TOP_LEVEL
}

enum class NavigationListType {
    TOURNAMENTS
}
