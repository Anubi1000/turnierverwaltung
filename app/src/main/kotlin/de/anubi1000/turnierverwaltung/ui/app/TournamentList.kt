package de.anubi1000.turnierverwaltung.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import de.anubi1000.turnierverwaltung.ui.tournament.list.TournamentListScreen
import de.anubi1000.turnierverwaltung.viewmodel.TournamentListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TournamentList(
    viewModel: TournamentListViewModel = koinViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.loadTournaments()
    }

    TournamentListScreen(
        state = viewModel.state
    )
}