package de.anubi1000.turnierverwaltung.ui.tournament.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
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
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentEditDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentListDestination
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.list.ListBase
import de.anubi1000.turnierverwaltung.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.util.getCurrentDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel

@Composable
fun TournamentList(
    navController: NavController,
    state: BaseListViewModel.State,
    modifier: Modifier = Modifier
) {
    ListBase(
        title = LocalStrings.current.tournaments,
        onCreateButtonClick = {
            if (navController.getCurrentDestination() !is TournamentEditDestination) {
                navController.navigate(TournamentEditDestination())
            }
        },
        modifier = modifier
    ) {
        @Suppress("UNCHECKED_CAST")
        when (state) {
            is BaseListViewModel.State.Loading -> LoadingIndicator()
            is BaseListViewModel.State.Loaded<*> -> LoadedContent(
                navController = navController,
                state = state as BaseListViewModel.State.Loaded<Tournament>
            )
        }
    }
}

@Composable
private fun LoadedContent(navController: NavController, state: BaseListViewModel.State.Loaded<Tournament>, modifier: Modifier = Modifier) {
    val tournaments by state.itemFlow.collectAsStateWithLifecycle()

    if (tournaments.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(LocalStrings.current.doesntExist(LocalStrings.current.tournaments))
        }
    } else {
        val currentDestination by navController.currentDestinationAsState()

        val currentTournamentId by remember(navController) {
            derivedStateOf {
                when (val destination = currentDestination) {
                    is TournamentDetailDestination -> destination.tournamentId
                    is TournamentEditDestination -> destination.tournamentId
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
