package de.anubi1000.turnierverwaltung.ui.tournament

import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
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
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentDetailViewModel

@Composable
fun TournamentDetailScreen(
    navController: NavController,
    state: TournamentDetailViewModel.State,
    onDeleteButtonClick: () -> Unit,
    showOnScoreboard: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DetailScreenBase(
        navController = navController,
        title = LocalStrings.current.tournament,
        onEditButtonClick = {
            if (state is TournamentDetailViewModel.State.Loaded) {
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
                enabled = state is TournamentDetailViewModel.State.Loaded
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
        when (state) {
            TournamentDetailViewModel.State.Loading -> LoadingIndicator()
            is TournamentDetailViewModel.State.Loaded -> LoadedDetailContent(
                state = state,
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (showDeleteDialog && state is TournamentDetailViewModel.State.Loaded) {
        DeleteDialog(
            itemName = state.item.name,
            onDismissRequest = { showDeleteDialog = false },
            onConfirmButtonClick = {
                showDeleteDialog = false
                onDeleteButtonClick()
            }
        )
    }
}

@Composable
private fun LoadedDetailContent(
    state: TournamentDetailViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    DetailContent(
        modifier = modifier
    ) {
        val strings = LocalStrings.current

        DetailCard(
            title = strings.general
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
