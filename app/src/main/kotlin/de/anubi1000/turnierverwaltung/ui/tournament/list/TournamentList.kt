package de.anubi1000.turnierverwaltung.ui.tournament.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.navigation.TournamentEditDestination
import de.anubi1000.turnierverwaltung.navigation.TournamentListDestination
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.util.getCurrentDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.TournamentListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentList(
    navController: NavController,
    state: TournamentListViewModel.State,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LocalStrings.current.tournaments) },
                actions = {
                    TooltipIconButton(
                        icon = Icons.Default.Add,
                        tooltip = LocalStrings.current.createTournament,
                        onClick = {
                            if (navController.getCurrentDestination() !is TournamentEditDestination) {
                                navController.navigate(TournamentEditDestination())
                            }
                        }
                    )
                }
            )
        },
        modifier = modifier
    ) { padding ->
        when (state) {
            is TournamentListViewModel.State.Loading -> LoadingIndicator(
                modifier = Modifier.padding(padding)
            )
            is TournamentListViewModel.State.Loaded -> LoadedContent(
                navController = navController,
                state = state,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun LoadedContent(navController: NavController, state: TournamentListViewModel.State.Loaded, modifier: Modifier = Modifier) {
    val tournaments by state.tournamentFlow.collectAsStateWithLifecycle()

    if (tournaments.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(LocalStrings.current.noTournamentsExist)
        }
    } else {
        val currentDestination by navController.currentDestinationAsState()

        val currentTournamentId by remember(navController) {
            derivedStateOf {
                when (val destination = currentDestination) {
                    is TournamentDetailDestination -> destination.tournamentId
                    else -> null
                }?.toObjectId()
            }
        }

        LazyColumn(
            modifier = modifier
        ) {
            items(tournaments, key = { it.id }) { item ->
                TournamentListItem(
                    tournament = item,
                    selected = currentTournamentId == item.id,
                    onClick = {
                        if (currentTournamentId != item.id) {
                            navController.navigate(TournamentDetailDestination(item.id)) {
                                popUpTo<TournamentListDestination>()
                            }
                        }
                    },
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}
