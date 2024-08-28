package de.anubi1000.turnierverwaltung.navigation

interface AppDestination {
    val navigationMenuOption: NavigationMenuOption?
}

enum class NavigationMenuOption {
    TOURNAMENTS,
    PARTICIPANTS,
    TEAMS,
    CLUBS,
}