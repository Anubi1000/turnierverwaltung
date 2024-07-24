package de.anubi1000.turnierverwaltung.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.anubi1000.turnierverwaltung.navigation.AppScreen
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuType
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentCreateDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentListDestination

@Composable
fun NavigationMenu() {
    val navigator = LocalNavigator.currentOrThrow
    val screen = navigator.lastItem as AppScreen

    when (screen.navigationMenuType) {
        NavigationMenuType.TOP_LEVEL -> NavigationRail {
            NavigationRailItem(
                selected = screen is TournamentListDestination || screen is TournamentDetailDestination || screen is TournamentCreateDestination,
                onClick = { navigator.popUntilRoot() },
                label = { Text(LocalStrings.current.tournaments) },
                icon = { Icon(Icons.Default.Dashboard, null) },
            )
        }
        else -> {}
    }
}