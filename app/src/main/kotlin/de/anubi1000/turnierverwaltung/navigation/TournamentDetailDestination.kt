package de.anubi1000.turnierverwaltung.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.ui.shared.TournamentListLayout
import de.anubi1000.turnierverwaltung.ui.tournament.detail.TournamentDetailScreen
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.TournamentDetailViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.mongodb.kbson.ObjectId

@Serializable
data class TournamentDetailDestination(
    val tournamentId: String
) : AppDestination {
    constructor(tournamentId: ObjectId) : this(tournamentId.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.TOURNAMENTS
}

fun NavGraphBuilder.tournamentDetailDestinations(navController: NavController) = composable<TournamentDetailDestination> { backStackEntry ->
    val args = backStackEntry.toRoute<TournamentDetailDestination>()

    TournamentListLayout(
        navController = navController
    ) {
        val viewModel: TournamentDetailViewModel = koinViewModel()

        LaunchedEffect(viewModel) {
            viewModel.loadTournament(args.tournamentId.toObjectId())
        }

        TournamentDetailScreen(
            navController = navController,
            state = viewModel.state,
            onDeleteButtonClick = {
                viewModel.deleteTournament()
            },
            showOnScoreboard = {
                viewModel.showTournamentOnScoreboard()
            }
        )
    }
}
