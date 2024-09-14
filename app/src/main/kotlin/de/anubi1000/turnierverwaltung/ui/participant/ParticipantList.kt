package de.anubi1000.turnierverwaltung.ui.participant

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
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantDetailDestination
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantEditDestination
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantListDestination
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantResultDestination
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.ui.util.getCurrentDestination
import de.anubi1000.turnierverwaltung.ui.util.screen.list.ListBase
import de.anubi1000.turnierverwaltung.ui.util.screen.list.SelectableListItem
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.participant.ParticipantListViewModel

@Composable
fun ParticipantList(
    navController: NavController,
    state: ParticipantListViewModel.State,
    modifier: Modifier = Modifier,
) {
    ListBase(
        title = LocalStrings.current.participants,
        onCreateButtonClick = {
            val currentDestination = navController.getCurrentDestination()
            if (currentDestination !is ParticipantEditDestination || currentDestination.id != null) {
                navController.navigate(ParticipantEditDestination())
            }
        },
        modifier = modifier,
    ) {
        when (state) {
            is ParticipantListViewModel.State.Loading -> LoadingIndicator()
            is ParticipantListViewModel.State.Loaded -> LoadedContent(
                navController = navController,
                state = state,
            )
        }
    }
}

@Composable
private fun LoadedContent(
    navController: NavController,
    state: ParticipantListViewModel.State.Loaded,
    modifier: Modifier = Modifier,
) {
    val items by state.itemFlow.collectAsStateWithLifecycle()

    if (items.isEmpty()) {
        val strings = LocalStrings.current

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = strings.xDontExist(strings.participants))
        }
    } else {
        val currentDestination by navController.currentDestinationAsState()

        val currentItemId by remember(navController) {
            derivedStateOf {
                when (val destination = currentDestination) {
                    is ParticipantDetailDestination -> destination.id
                    is ParticipantEditDestination -> destination.id
                    is ParticipantResultDestination -> destination.participantId
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
                    leadingContent = { Text(item.startNumber.toString()) },
                    selected = currentItemId == item.id,
                    onClick = {
                        if (currentItemId != item.id) {
                            navController.navigate(ParticipantDetailDestination(item.id)) {
                                popUpTo<ParticipantListDestination>()
                            }
                        }
                    },
                    modifier = itemModifier,
                )
            }
        }
    }
}
