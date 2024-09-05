package de.anubi1000.turnierverwaltung.ui.tournament

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
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
import de.anubi1000.turnierverwaltung.ui.util.screen.list.ListBase
import de.anubi1000.turnierverwaltung.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.util.formatAsDate
import de.anubi1000.turnierverwaltung.util.getCurrentDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentListViewModel

@Composable
fun TournamentList(
    navController: NavController,
    state: TournamentListViewModel.State,
    modifier: Modifier = Modifier
) {
    ListBase(
        title = LocalStrings.current.tournaments,
        onCreateButtonClick = {
            val currentDestination = navController.getCurrentDestination()
            if (currentDestination !is TournamentEditDestination || currentDestination.id != null) {
                navController.navigate(TournamentEditDestination())
            }
        },
        modifier = modifier
    ) {
        when (state) {
            TournamentListViewModel.State.Loading -> LoadingIndicator()
            is TournamentListViewModel.State.Loaded -> LoadedListContent(
                navController = navController,
                state = state
            )
        }
    }
}

@Composable
private fun LoadedListContent(
    navController: NavController,
    state: TournamentListViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    val items by state.itemFlow.collectAsStateWithLifecycle()

    if (items.isEmpty()) {
        val strings = LocalStrings.current

        CenteredText(
            text = strings.xDontExist(strings.tournaments)
        )
    } else {
        val currentDestination by navController.currentDestinationAsState()

        val currentItemId by remember(navController) {
            derivedStateOf {
                when (val destination = currentDestination) {
                    is TournamentDetailDestination -> destination.id
                    is TournamentEditDestination -> destination.id
                    else -> null
                }?.toObjectId()
            }
        }

        val itemModifier = Modifier.padding(2.dp)
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            items(items, key = { it.id }) { item ->
                SelectableListItem(
                    headlineContent = { Text(text = item.name) },
                    supportingContent = { Text(text = item.date.formatAsDate()) },
                    selected = currentItemId == item.id,
                    onClick = {
                        if (currentItemId != item.id) {
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
