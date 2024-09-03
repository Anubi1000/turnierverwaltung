package de.anubi1000.turnierverwaltung.ui.tournament.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentEditDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentListDestination
import de.anubi1000.turnierverwaltung.ui.util.CenteredText
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.SelectableListItem
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.util.formatAsDate
import de.anubi1000.turnierverwaltung.util.getCurrentDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentListViewModel

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
                title = { Text(text = LocalStrings.current.tournaments) },
                actions = {
                    TooltipIconButton(
                        icon = Icons.Default.Add,
                        tooltip = LocalStrings.current.createTournament,
                        onClick = {
                            val currentDestination = navController.getCurrentDestination()
                            if (currentDestination !is TournamentEditDestination || currentDestination.tournamentId != null) {
                                navController.navigate(TournamentEditDestination())
                            }
                        }
                    )
                }
            )
        },
        modifier = modifier
    ) { padding ->
        val contentModifier = Modifier.padding(padding).background(color = MaterialTheme.colorScheme.background)
        when (state) {
            is TournamentListViewModel.State.Loading -> LoadingIndicator(
                modifier = contentModifier
            )
            is TournamentListViewModel.State.Loaded -> LoadedContent(
                navController = navController,
                state = state,
                modifier = contentModifier
            )
        }
    }
}

@Composable
private fun LoadedContent(
    navController: NavController,
    state: TournamentListViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    val tournaments by state.tournamentFlow.collectAsStateWithLifecycle()

    if (tournaments.isEmpty()) {
        val strings = LocalStrings.current

        CenteredText(
            text = strings.xDontExist(strings.tournaments)
        )
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

        val itemModifier = Modifier.padding(2.dp)

        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            items(tournaments, key = { it.id }) { item ->
                SelectableListItem(
                    headlineContent = { Text(text = item.name) },
                    supportingContent = { Text(text = item.date.formatAsDate()) },
                    selected = currentTournamentId == item.id,
                    onClick = {
                        if (currentTournamentId != item.id) {
                            navController.navigate(TournamentDetailDestination(item.id)) {
                                popUpTo<TournamentListDestination>()
                            }
                        }
                    },
                    modifier = itemModifier
                )
            }
        }
    }
}
