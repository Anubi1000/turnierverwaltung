package de.anubi1000.turnierverwaltung.ui.tournament.detail

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantListDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentEditDestination
import de.anubi1000.turnierverwaltung.ui.util.DeleteDialog
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailCard
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailContent
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailItem
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailScreenBase
import de.anubi1000.turnierverwaltung.util.Icon
import de.anubi1000.turnierverwaltung.util.formatAsDate
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseDetailViewModel

@Composable
fun TournamentDetailScreen(
    navController: NavController,
    state: BaseDetailViewModel.State,
    onDeleteButtonClick: () -> Unit,
    showOnScoreboard: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DetailScreenBase(
        navController = navController,
        title = LocalStrings.current.tournament,
        onEditButtonClick = {
            if (state is BaseDetailViewModel.State.Loaded<*>) {
                @Suppress("UNCHECKED_CAST")
                state as BaseDetailViewModel.State.Loaded<Tournament>

                navController.navigate(TournamentEditDestination(state.item.id))
            }
        },
        onDeleteButtonClick = {
            showDeleteDialog = true
        },
        additionalActions = {
            TooltipIconButton(
                icon = Icons.Default.Scoreboard,
                tooltip = LocalStrings.current.showOnScoreboard,
                onClick = showOnScoreboard,
                enabled = state is BaseDetailViewModel.State.Loaded<*>
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(ParticipantListDestination)
                },
                text = { Text(LocalStrings.current.openTournament) },
                icon = { Icon(Icons.AutoMirrored.Filled.OpenInNew) }
            )
        }
    ) { padding ->
        @Suppress("UNCHECKED_CAST")
        when (state) {
            is BaseDetailViewModel.State.Loading -> LoadingIndicator()
            is BaseDetailViewModel.State.Loaded<*> -> LoadedContent(
                state = state as BaseDetailViewModel.State.Loaded<Tournament>,
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (showDeleteDialog && state is BaseDetailViewModel.State.Loaded<*>) {
        @Suppress("UNCHECKED_CAST")
        state as BaseDetailViewModel.State.Loaded<Tournament>

        DeleteDialog(
            itemName = state.item.name,
            onDismissRequest = { showDeleteDialog = false },
            onConfirmButtonClick = {
                showDeleteDialog = false
                navController.popBackStack()
                onDeleteButtonClick()
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LoadedContent(
    state: BaseDetailViewModel.State.Loaded<Tournament>,
    modifier: Modifier = Modifier
) {
    DetailContent(
        modifier = modifier
    ) {
        val strings = LocalStrings.current

        DetailCard(
            title = strings.general,
            modifier = Modifier.width(450.dp).fillMaxRowHeight()
        ) {
            DetailItem(
                headlineText = state.item.name,
                overlineText = strings.name
            )
            DetailItem(
                headlineText = state.item.date.formatAsDate(),
                overlineText = strings.dateOfTournament
            )
        }
    }
}
