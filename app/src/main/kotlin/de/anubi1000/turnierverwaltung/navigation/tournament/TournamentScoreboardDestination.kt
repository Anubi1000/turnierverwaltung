package de.anubi1000.turnierverwaltung.navigation.tournament

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.tournament.TournamentScoreboardScreen
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentScoreboardViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.mongodb.kbson.ObjectId

@Serializable
data class TournamentScoreboardDestination(val id: String) : AppDestination {
    constructor(id: ObjectId) : this(id.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.TOURNAMENT_OVERVIEW
}

fun NavGraphBuilder.tournamentScoreboardDestination(navController: NavController) = composable<TournamentScoreboardDestination> { backStackEntry ->
    val args = backStackEntry.toRoute<TournamentScoreboardDestination>()
    val viewModel: TournamentScoreboardViewModel = koinViewModel()

    LaunchedEffect(viewModel) {
        viewModel.loadItem(args.id.toObjectId())
    }

    TournamentScoreboardScreen(
        navController = navController,
        state = viewModel.state,
    )
}
