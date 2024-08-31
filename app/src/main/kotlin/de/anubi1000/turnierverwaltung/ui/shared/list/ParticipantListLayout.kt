package de.anubi1000.turnierverwaltung.ui.shared.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.ui.participant.list.ParticipantList
import de.anubi1000.turnierverwaltung.ui.util.ListDetailLayout
import de.anubi1000.turnierverwaltung.util.getDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.participant.ParticipantListViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ParticipantListLayout(
    navController: NavController,
    viewModel: ParticipantListViewModel = koinViewModel(
        viewModelStoreOwner = navController.getBackStackEntry<TournamentDetailDestination>()
    ) {
        parametersOf(navController.getDestination<TournamentDetailDestination>().tournamentId.toObjectId())
    },
    content: @Composable () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.loadItems()
    }

    ListDetailLayout(
        listContent = {
            ParticipantList(
                navController = navController,
                state = viewModel.state,
                modifier = Modifier.padding(end = 8.dp)
            )
        },
        detailContent = content,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}