package de.anubi1000.turnierverwaltung.ui.shared.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentListDestination
import de.anubi1000.turnierverwaltung.ui.tournament.list.TournamentList
import de.anubi1000.turnierverwaltung.ui.util.ListDetailLayout
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TournamentListLayout(
    navController: NavController,
    viewModel: TournamentListViewModel = koinViewModel(
        viewModelStoreOwner = navController.getBackStackEntry<TournamentListDestination>()
    ),
    content: @Composable () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.loadItems()
    }

    ListDetailLayout(
        listContent = {
            TournamentList(
                navController = navController,
                state = viewModel.state,
                modifier = Modifier.padding(end = 8.dp),
            )
        },
        detailContent = content,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}