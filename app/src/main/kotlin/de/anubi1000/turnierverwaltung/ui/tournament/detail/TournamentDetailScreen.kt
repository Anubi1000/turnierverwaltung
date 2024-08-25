package de.anubi1000.turnierverwaltung.ui.tournament.detail

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
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
import de.anubi1000.turnierverwaltung.navigation.TournamentEditDestination
import de.anubi1000.turnierverwaltung.ui.tournament.TournamentDeleteDialog
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailCard
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailContent
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailItem
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailScreenBase
import de.anubi1000.turnierverwaltung.util.Icon
import de.anubi1000.turnierverwaltung.util.formatAsDate
import de.anubi1000.turnierverwaltung.viewmodel.TournamentDetailViewModel

@Composable
fun TournamentDetailScreen(
    navController: NavController,
    state: TournamentDetailViewModel.State,
    oneDeleteButtonClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val strings = LocalStrings.current

    DetailScreenBase(
        navController = navController,
        title = remember(state) {
            var text = strings.tournament
            if (state is TournamentDetailViewModel.State.Loaded) {
                text += ": ${state.tournament.name}"
            }
            text
        },
        onEditButtonClick = {
            if (state is TournamentDetailViewModel.State.Loaded) {
                navController.navigate(TournamentEditDestination(state.tournament.id))
            }
        },
        onDeleteButtonClick = {
            showDeleteDialog = true
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    TODO("Open tournament")
                },
                text = { Text(LocalStrings.current.openTournament) },
                icon = { Icon(Icons.AutoMirrored.Filled.OpenInNew) }
            )
        }
    ) { padding ->
        when (state) {
            is TournamentDetailViewModel.State.Loading -> LoadingIndicator()
            is TournamentDetailViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (showDeleteDialog && state is TournamentDetailViewModel.State.Loaded) {
        TournamentDeleteDialog(
            tournamentName = state.tournament.name,
            onDismissRequest = { showDeleteDialog = false },
            onConfirmButtonClicked = {
                showDeleteDialog = false
                navController.popBackStack()
                oneDeleteButtonClick()
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LoadedContent(
    state: TournamentDetailViewModel.State.Loaded,
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
                headlineText = state.tournament.name,
                overlineText = strings.name
            )
            DetailItem(
                headlineText = state.tournament.date.formatAsDate(),
                overlineText = strings.dateOfTournament
            )
        }

        DetailCard(
            title = strings.general,
            modifier = Modifier.width(450.dp).fillMaxRowHeight()
        ) {
            FlowRow(
                maxItemsInEachRow = 2
            ) {
                val weightModifier = Modifier.weight(1f)
                DetailItem(
                    headlineText = state.tournament.participants.size.toString(),
                    overlineText = "Anzahl Teilnehmer",
                    modifier = weightModifier
                )
                DetailItem(
                    headlineText = state.tournament.clubs.size.toString(),
                    overlineText = "Anzahl Vereine",
                    modifier = weightModifier
                )
                DetailItem(
                    headlineText = state.tournament.teams.size.toString(),
                    overlineText = "Anzahl Teams",
                    modifier = weightModifier
                )
                DetailItem(
                    headlineText = (state.tournament.disciplines.size + state.tournament.teamDisciplines.size).toString(),
                    overlineText = "Anzahl Disziplinen",
                    modifier = weightModifier
                )
            }
        }
    }
}
