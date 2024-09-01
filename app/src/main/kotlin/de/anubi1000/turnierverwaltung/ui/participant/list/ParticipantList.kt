package de.anubi1000.turnierverwaltung.ui.participant.list

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
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantDetailDestination
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantEditDestination
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.list.ListBase
import de.anubi1000.turnierverwaltung.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.util.getCurrentDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel

@Composable
fun ParticipantList(
    navController: NavController,
    state: BaseListViewModel.State,
    modifier: Modifier = Modifier
) {
    ListBase(
        title = LocalStrings.current.participants,
        onCreateButtonClick = {
            if (navController.getCurrentDestination() !is ParticipantEditDestination) {
                navController.navigate(ParticipantEditDestination())
            }
        },
        modifier = modifier,
    ) {
        @Suppress("UNCHECKED_CAST")
        when (state) {
            is BaseListViewModel.State.Loading -> LoadingIndicator()
            is BaseListViewModel.State.Loaded<*> -> LoadedContent(
                navController = navController,
                state = state as BaseListViewModel.State.Loaded<Participant>
            )
        }
    }
}

@Composable
private fun LoadedContent(navController: NavController, state: BaseListViewModel.State.Loaded<Participant>, modifier: Modifier = Modifier) {
    val participants by state.itemFlow.collectAsStateWithLifecycle()

    if (participants.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(LocalStrings.current.xDontExist(LocalStrings.current.participants))
        }
    } else {
        val currentDestination by navController.currentDestinationAsState()

        val currentTournamentId by remember(navController) {
            derivedStateOf {
                when (val destination = currentDestination) {
                    is ParticipantDetailDestination -> destination.participantId
                    is ParticipantEditDestination -> destination.participantId
                    else -> null
                }?.toObjectId()
            }
        }

        LazyColumn(
            modifier = modifier
        ) {
            items(participants, key = { it.id }) { item ->
                ParticipantListItem(
                    participant = item,
                    selected = currentTournamentId == item.id,
                    onClick = {
                        if (currentTournamentId != item.id) {
                            navController.navigate(ParticipantDetailDestination(item.id))
                        }
                    },
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}