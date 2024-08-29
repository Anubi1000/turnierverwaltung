package de.anubi1000.turnierverwaltung.ui.shared.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantListDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.ui.participant.list.ParticipantList
import de.anubi1000.turnierverwaltung.ui.team.list.TeamList
import de.anubi1000.turnierverwaltung.ui.util.ListDetailLayout
import de.anubi1000.turnierverwaltung.viewmodel.ParticipantListViewModel
import de.anubi1000.turnierverwaltung.viewmodel.TeamListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeamListLayout(
    navController: NavController,
    viewModel: TeamListViewModel = koinViewModel(
        viewModelStoreOwner = navController.getBackStackEntry<ParticipantListDestination>()
    ),
    content: @Composable () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.loadItems()
    }

    ListDetailLayout(
        listContent = {
            TeamList(
                navController = navController,
                state = viewModel.state,
                modifier = Modifier.padding(end = 8.dp),
            )
        },
        detailContent = content,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}