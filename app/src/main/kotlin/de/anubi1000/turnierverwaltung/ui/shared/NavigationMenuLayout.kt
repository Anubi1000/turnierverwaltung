package de.anubi1000.turnierverwaltung.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.navigation.club.ClubListDestination
import de.anubi1000.turnierverwaltung.navigation.discipline.DisciplineListDestination
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantListDestination
import de.anubi1000.turnierverwaltung.navigation.team.TeamListDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentListDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentScoreboardDestination
import de.anubi1000.turnierverwaltung.ui.club.ClubList
import de.anubi1000.turnierverwaltung.ui.discipline.DisciplineList
import de.anubi1000.turnierverwaltung.ui.participant.ParticipantList
import de.anubi1000.turnierverwaltung.ui.team.TeamList
import de.anubi1000.turnierverwaltung.ui.tournament.TournamentList
import de.anubi1000.turnierverwaltung.ui.util.Icon
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.ui.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.ui.util.getDestination
import de.anubi1000.turnierverwaltung.ui.util.topAppBarPadding
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.club.ClubListViewModel
import de.anubi1000.turnierverwaltung.viewmodel.discipline.DisciplineListViewModel
import de.anubi1000.turnierverwaltung.viewmodel.participant.ParticipantListViewModel
import de.anubi1000.turnierverwaltung.viewmodel.team.TeamListViewModel
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentListViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NavigationMenuLayout(
    navController: NavController,
    content: @Composable () -> Unit,
) {
    val currentDestination by navController.currentDestinationAsState()

    Surface(
        color = MaterialTheme.colorScheme.background,
    ) {
        Row {
            if (currentDestination != null) {
                val destination = currentDestination!!
                when (destination.navigationMenuOption) {
                    NavigationMenuOption.TOURNAMENTS -> TopLevelNavigationMenu(
                        navController = navController,
                        currentDestination = destination,
                    )
                    NavigationMenuOption.TOURNAMENT_OVERVIEW,
                    NavigationMenuOption.PARTICIPANTS,
                    NavigationMenuOption.TEAMS,
                    NavigationMenuOption.CLUBS,
                    NavigationMenuOption.DISCIPLINES,
                    -> TournamentNavigationMenu(
                        navController = navController,
                        currentDestination = destination,
                    )
                    else -> {}
                }

                when (destination.navigationMenuOption) {
                    NavigationMenuOption.TOURNAMENTS -> TournamentListLayout(navController = navController)
                    NavigationMenuOption.PARTICIPANTS -> ParticipantListLayout(navController = navController)
                    NavigationMenuOption.TEAMS -> TeamListLayout(navController = navController)
                    NavigationMenuOption.CLUBS -> ClubListLayout(navController = navController)
                    NavigationMenuOption.DISCIPLINES -> DisciplineListLayout(navController = navController)
                    else -> {}
                }
            }

            content()
        }
    }
}

@Composable
private fun TournamentListLayout(
    navController: NavController,
    viewModel: TournamentListViewModel = koinViewModel(
        viewModelStoreOwner = navController.getBackStackEntry<TournamentListDestination>(),
    ),
) {
    LaunchedEffect(viewModel) {
        viewModel.loadList()
    }

    TournamentList(
        navController = navController,
        state = viewModel.state,
        modifier = Modifier.padding(end = 8.dp).width(400.dp),
    )
}

@Composable
private fun ParticipantListLayout(
    navController: NavController,
    viewModel: ParticipantListViewModel = koinViewModel(
        viewModelStoreOwner = navController.getBackStackEntry<TournamentScoreboardDestination>(),
    ) {
        parametersOf(navController.getDestination<TournamentScoreboardDestination>().id.toObjectId())
    },
) {
    LaunchedEffect(viewModel) {
        viewModel.loadList()
    }

    ParticipantList(
        navController = navController,
        state = viewModel.state,
        modifier = Modifier.padding(end = 8.dp).width(400.dp),
    )
}

@Composable
private fun TeamListLayout(
    navController: NavController,
    viewModel: TeamListViewModel = koinViewModel(
        viewModelStoreOwner = navController.getBackStackEntry<TournamentScoreboardDestination>(),
    ) {
        parametersOf(navController.getDestination<TournamentScoreboardDestination>().id.toObjectId())
    },
) {
    LaunchedEffect(viewModel) {
        viewModel.loadList()
    }

    TeamList(
        navController = navController,
        state = viewModel.state,
        modifier = Modifier.padding(end = 8.dp).width(400.dp),
    )
}

@Composable
private fun ClubListLayout(
    navController: NavController,
    viewModel: ClubListViewModel = koinViewModel(
        viewModelStoreOwner = navController.getBackStackEntry<TournamentScoreboardDestination>(),
    ) {
        parametersOf(navController.getDestination<TournamentScoreboardDestination>().id.toObjectId())
    },
) {
    LaunchedEffect(viewModel) {
        viewModel.loadList()
    }

    ClubList(
        navController = navController,
        state = viewModel.state,
        modifier = Modifier.padding(end = 8.dp).width(400.dp),
    )
}

@Composable
private fun DisciplineListLayout(
    navController: NavController,
    viewModel: DisciplineListViewModel = koinViewModel(
        viewModelStoreOwner = navController.getBackStackEntry<TournamentScoreboardDestination>(),
    ) {
        parametersOf(navController.getDestination<TournamentScoreboardDestination>().id.toObjectId())
    },
) {
    LaunchedEffect(viewModel) {
        viewModel.loadList()
    }

    DisciplineList(
        navController = navController,
        state = viewModel.state,
        modifier = Modifier.padding(end = 8.dp).width(400.dp),
    )
}

@Composable
private fun TopLevelNavigationMenu(
    navController: NavController,
    currentDestination: AppDestination,
) {
    NavigationRail {
        val currentMenuOption = currentDestination.navigationMenuOption

        NavigationRailItem(
            selected = currentMenuOption == NavigationMenuOption.TOURNAMENTS,
            onClick = {
                if (currentMenuOption != NavigationMenuOption.TOURNAMENTS) {
                    navController.navigate(TournamentListDestination)
                }
            },
            label = { Text(LocalStrings.current.tournaments) },
            icon = { Icon(Icons.Default.Dashboard) },
            modifier = Modifier.topAppBarPadding(),
        )
        NavigationRailItem(
            selected = false,
            onClick = {},
            label = { Text(LocalStrings.current.scoreboard) },
            icon = { Icon(Icons.Default.Scoreboard) },
        )
    }
}

@Composable
private fun TournamentNavigationMenu(
    navController: NavController,
    currentDestination: AppDestination,
) {
    NavigationRail(
        modifier = Modifier.width(80.dp),
    ) {
        val currentMenuOption = currentDestination.navigationMenuOption

        Box(
            modifier = Modifier.size(width = 80.dp, height = 60.dp),
            contentAlignment = Alignment.Center,
        ) {
            TooltipIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                tooltip = LocalStrings.current.back,
                onClick = { navController.popBackStack<TournamentDetailDestination>(false) },
            )
        }

        val menuItems = remember(navController) {
            listOf(
                NavigationMenuItem(
                    NavigationMenuOption.TOURNAMENT_OVERVIEW, LocalStrings.current.overview, Icons.Default.Dashboard
                ) {
                    navController.popBackStack<TournamentScoreboardDestination>(false)
                },
                NavigationMenuItem(
                    NavigationMenuOption.PARTICIPANTS, LocalStrings.current.participants, Icons.Default.Person
                ) {
                    navController.navigate(ParticipantListDestination)
                },
                NavigationMenuItem(
                    NavigationMenuOption.TEAMS, LocalStrings.current.teams, Icons.Default.People
                ) {
                    navController.navigate(TeamListDestination)
                },
                NavigationMenuItem(
                    NavigationMenuOption.CLUBS, LocalStrings.current.clubs, Icons.Default.Groups
                ) {
                    navController.navigate(ClubListDestination)
                },
                NavigationMenuItem(
                    NavigationMenuOption.DISCIPLINES, LocalStrings.current.disciplines, Icons.Default.Queue
                ) {
                    navController.navigate(DisciplineListDestination)
                }
            )
        }

        menuItems.forEach { item ->
            NavigationRailItem(
                selected = currentMenuOption == item.option,
                onClick = {
                    if (currentMenuOption != item.option) item.onClick()
                },
                label = { Text(item.label) },
                icon = { Icon(item.icon) }
            )
        }
    }
}

data class NavigationMenuItem(
    val option: NavigationMenuOption,
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
