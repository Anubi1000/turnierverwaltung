package de.anubi1000.turnierverwaltung.ui.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentListDestination
import de.anubi1000.turnierverwaltung.util.Icon
import de.anubi1000.turnierverwaltung.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.util.topAppBarPadding

@Composable
fun TopLevelNavigationLayout(
    navController: NavController,
    content: @Composable () -> Unit
) {
    Row {
        NavigationRail {
            val currentDestination by navController.currentDestinationAsState()
            var currentMenuOption by remember { mutableStateOf(NavigationMenuOption.TOURNAMENTS) }
            LaunchedEffect(currentDestination) {
                val option = currentDestination?.navigationMenuOption
                if (option != null) {
                    currentMenuOption = option
                }
            }

            NavigationRailItem(
                selected = currentMenuOption == NavigationMenuOption.TOURNAMENTS,
                onClick = {
                    if (currentMenuOption != NavigationMenuOption.TOURNAMENTS) {
                        navController.navigate(TournamentListDestination)
                    }
                },
                label = { Text(LocalStrings.current.tournaments) },
                icon = { Icon(Icons.Default.Dashboard) },
                modifier = Modifier.topAppBarPadding()
            )
            NavigationRailItem(
                selected = false,
                onClick = {},
                label = { Text(LocalStrings.current.scoreboard) },
                icon = { Icon(Icons.Default.Scoreboard) }
            )
        }

        content()
    }
}