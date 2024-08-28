package de.anubi1000.turnierverwaltung.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.ui.participant.list.ParticipantList
import de.anubi1000.turnierverwaltung.ui.tournament.list.TournamentList
import de.anubi1000.turnierverwaltung.viewmodel.ParticipantListViewModel
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParticipantListLayout(
    navController: NavController,
    viewModel: ParticipantListViewModel = koinViewModel(
        viewModelStoreOwner = navController.getBackStackEntry<TournamentDetailDestination>()
    ),
    content: @Composable () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.loadItems()
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        val availableWidth = maxWidth - 8.dp
        val firstWidth = (availableWidth * 0.4f).coerceIn(300.dp, 420.dp)
        val secondWidth = availableWidth - firstWidth

        Row {
            ParticipantList(
                navController = navController,
                state = viewModel.state,
                modifier = Modifier.width(firstWidth).padding(end = 8.dp),
            )

            Box(
                modifier = Modifier.width(secondWidth)
            ) {
                content()
            }
        }
    }
}