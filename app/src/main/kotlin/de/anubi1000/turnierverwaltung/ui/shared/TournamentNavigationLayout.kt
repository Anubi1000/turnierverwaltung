package de.anubi1000.turnierverwaltung.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantListDestination
import de.anubi1000.turnierverwaltung.navigation.team.TeamListDestination
import de.anubi1000.turnierverwaltung.ui.shared.list.TeamListLayout
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.util.Icon
import de.anubi1000.turnierverwaltung.util.currentDestinationAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentNavigationLayout(
    navController: NavController,
    content: @Composable () -> Unit
) {
    Row {
        NavigationRail(
            modifier = Modifier.width(80.dp)
        ) {
            val currentDestination by navController.currentDestinationAsState()
            var currentMenuOption by remember { mutableStateOf(NavigationMenuOption.PARTICIPANTS) }
            LaunchedEffect(currentDestination) {
                val option = currentDestination?.navigationMenuOption
                if (option != null) {
                    currentMenuOption = option
                }
            }

            Box(
                modifier = Modifier.size(width = 80.dp, height = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                TooltipIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    tooltip = LocalStrings.current.back,
                    onClick = { navController.navigateUp() }
                )
            }

            NavigationRailItem(
                selected = currentMenuOption == NavigationMenuOption.PARTICIPANTS,
                onClick = {
                    if (currentMenuOption != NavigationMenuOption.PARTICIPANTS) {
                        navController.popBackStack<ParticipantListDestination>(false)
                    }
                },
                label = { Text(LocalStrings.current.participants) },
                icon = { Icon(Icons.Default.Person) }
            )

            NavigationRailItem(
                selected = currentMenuOption == NavigationMenuOption.TEAMS,
                onClick = {
                    if (currentMenuOption != NavigationMenuOption.TEAMS) {
                       navController.navigate(TeamListDestination)
                    }
                },
                label = { Text(LocalStrings.current.teams) },
                icon = { Icon(Icons.Default.People) }
            )

            NavigationRailItem(
                selected = currentMenuOption == NavigationMenuOption.CLUBS,
                onClick = {
                    if (currentMenuOption != NavigationMenuOption.CLUBS) {
                        TODO("Vereine")
                    }
                },
                label = { Text("Vereine") },
                icon = { Icon(Icons.Default.Groups) }
            )

            NavigationRailItem(
                selected = currentMenuOption == NavigationMenuOption.PARTICIPANTS,
                onClick = {
                    if (currentMenuOption != NavigationMenuOption.PARTICIPANTS) {
                        navController.navigate(ParticipantListDestination)
                    }
                },
                label = { Text("Disziplinen") },
                icon = { Icon(Icons.Default.AddBox) }
            )

            NavigationRailItem(
                selected = currentMenuOption == NavigationMenuOption.PARTICIPANTS,
                onClick = {
                    if (currentMenuOption != NavigationMenuOption.PARTICIPANTS) {
                        navController.navigate(ParticipantListDestination)
                    }
                },
                label = {
                    Text(
                        text = "Team-Disziplinen",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    ) },
                icon = { Icon(Icons.Default.Queue) }
            )
        }

        content()
    }
}