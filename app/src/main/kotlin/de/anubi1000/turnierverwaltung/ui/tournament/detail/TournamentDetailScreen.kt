package de.anubi1000.turnierverwaltung.ui.tournament.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.anubi1000.turnierverwaltung.data.tournament.Tournament
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentEditDestination
import de.anubi1000.turnierverwaltung.ui.tournament.TournamentDeleteDialog
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.viewmodel.TournamentDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(
    state: TournamentDetailViewModel.State,
    deleteTournament: (Tournament) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LocalStrings.current.tournament) },
                navigationIcon = {
                    TooltipIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        tooltip = LocalStrings.current.back,
                        onClick = {
                            navigator.pop()
                        }
                    )
                },
                actions = {
                    TooltipIconButton(
                        icon = Icons.Default.Delete,
                        tooltip = LocalStrings.current.delete,
                        onClick = {
                            showDeleteDialog = true
                        }
                    )

                    TooltipIconButton(
                        icon = Icons.Default.Edit,
                        tooltip = LocalStrings.current.edit,
                        onClick = {
                            if (state is TournamentDetailViewModel.State.Loaded) {
                                navigator.push(TournamentEditDestination(state.tournament.id))
                            }
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {},
                text = { Text(LocalStrings.current.openTournament) },
                icon = { Icon(Icons.AutoMirrored.Filled.OpenInNew, null) }
            )
        }
    ) {
        Card(
            modifier = Modifier.padding(it).padding(bottom = 16.dp, end = 16.dp).fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            when (state) {
                is TournamentDetailViewModel.State.Loading -> LoadingIndicator()
                is TournamentDetailViewModel.State.Loaded -> {
                    LoadedContent(state)
                }
            }
        }
    }

    if (showDeleteDialog && state is TournamentDetailViewModel.State.Loaded) {
        TournamentDeleteDialog(
            tournamentName = state.tournament.name,
            onDismissRequest = { showDeleteDialog = false },
            onConfirmButtonClicked = {
                showDeleteDialog = false
                navigator.pop()
                deleteTournament(state.tournament)
            }
        )
    }
}
