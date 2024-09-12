package de.anubi1000.turnierverwaltung.ui.team

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
import de.anubi1000.turnierverwaltung.navigation.team.TeamDetailDestination
import de.anubi1000.turnierverwaltung.navigation.team.TeamEditDestination
import de.anubi1000.turnierverwaltung.navigation.team.TeamListDestination
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.list.ListBase
import de.anubi1000.turnierverwaltung.ui.util.screen.list.SelectableListItem
import de.anubi1000.turnierverwaltung.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.util.getCurrentDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.team.TeamListViewModel

@Composable
fun TeamList(
    navController: NavController,
    state: TeamListViewModel.State,
    modifier: Modifier = Modifier,
) {
    ListBase(
        title = LocalStrings.current.teams,
        onCreateButtonClick = {
            val currentDestination = navController.getCurrentDestination()
            if (currentDestination !is TeamEditDestination || currentDestination.id != null) {
                navController.navigate(TeamEditDestination())
            }
        },
        modifier = modifier,
    ) {
        when (state) {
            is TeamListViewModel.State.Loading -> LoadingIndicator()
            is TeamListViewModel.State.Loaded -> LoadedContent(
                navController = navController,
                state = state,
            )
        }
    }
}

@Composable
private fun LoadedContent(
    navController: NavController,
    state: TeamListViewModel.State.Loaded,
    modifier: Modifier = Modifier,
) {
    val items by state.itemFlow.collectAsStateWithLifecycle()

    if (items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = LocalStrings.current.xDontExist(LocalStrings.current.teams))
        }
    } else {
        val currentDestination by navController.currentDestinationAsState()

        val currentItemId by remember(navController) {
            derivedStateOf {
                when (val destination = currentDestination) {
                    is TeamDetailDestination -> destination.id
                    is TeamEditDestination -> destination.id
                    else -> null
                }?.toObjectId()
            }
        }

        val itemModifier = Modifier.padding(2.dp)
        LazyColumn(
            modifier = modifier,
        ) {
            items(items, key = { it.id }) { item ->
                SelectableListItem(
                    headlineContent = { Text(item.name) },
                    selected = currentItemId == item.id,
                    onClick = {
                        if (currentItemId != item.id) {
                            navController.navigate(TeamDetailDestination(item.id)) {
                                popUpTo<TeamListDestination>()
                            }
                        }
                    },
                    modifier = itemModifier,
                )
            }
        }
    }
}
