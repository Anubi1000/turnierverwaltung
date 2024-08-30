package de.anubi1000.turnierverwaltung.ui.shared.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantListDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.ui.club.list.ClubList
import de.anubi1000.turnierverwaltung.ui.util.ListDetailLayout
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.club.ClubListViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ClubListLayout(
    navController: NavController,
    viewModel: ClubListViewModel = koinViewModel(
        viewModelStoreOwner = navController.getBackStackEntry<ParticipantListDestination>()
    ) {
        val args: TournamentDetailDestination = navController.getBackStackEntry<TournamentDetailDestination>().toRoute()
        parametersOf(args.tournamentId.toObjectId())
    },
    content: @Composable () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.loadItems()
    }

    ListDetailLayout(
        listContent = {
            ClubList(
                navController = navController,
                state = viewModel.state,
                modifier = Modifier.padding(end = 8.dp)
            )
        },
        detailContent = content,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}
