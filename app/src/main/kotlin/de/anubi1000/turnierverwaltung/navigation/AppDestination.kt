package de.anubi1000.turnierverwaltung.navigation

interface AppDestination {
    val navigationMenuOption: NavigationMenuOption?
}

enum class NavigationMenuOption {
    TOURNAMENTS,
    TOURNAMENT_OVERVIEW,
    PARTICIPANTS,
    TEAMS,
    CLUBS,
    DISCIPLINES,
}
