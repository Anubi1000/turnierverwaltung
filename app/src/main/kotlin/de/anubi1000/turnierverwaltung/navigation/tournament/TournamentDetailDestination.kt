package de.anubi1000.turnierverwaltung.navigation.tournament

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.tournament.TournamentDetailScreen
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentDetailViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.mongodb.kbson.ObjectId

@Serializable
data class TournamentDetailDestination(
    val id: String,
) : AppDestination {
    constructor(id: ObjectId) : this(id.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.TOURNAMENTS
}

fun NavGraphBuilder.tournamentDetailDestinations(navController: NavController) = composable<TournamentDetailDestination> { backStackEntry ->
    val args = backStackEntry.toRoute<TournamentDetailDestination>()
    val viewModel: TournamentDetailViewModel = koinViewModel()

    LaunchedEffect(viewModel) {
        viewModel.loadItem(args.id.toObjectId())
    }

    TournamentDetailScreen(
        navController = navController,
        state = viewModel.state,
        onDeleteButtonClick = {
            viewModel.deleteItem {
                navController.popBackStack()
            }
        },
        showOnScoreboard = {
            viewModel.showTournamentOnScoreboard()
        },
    )
}
