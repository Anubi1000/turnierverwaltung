package de.anubi1000.turnierverwaltung.navigation.team

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.ui.team.TeamEditScreen
import de.anubi1000.turnierverwaltung.util.getDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.team.TeamEditViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.mongodb.kbson.ObjectId

@Serializable
data class TeamEditDestination(val id: String?) : AppDestination {
    constructor(id: ObjectId? = null) : this(id?.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.TEAMS
}

fun NavGraphBuilder.teamEditDestination(navController: NavController) = composable<TeamEditDestination> { backStackEntry ->
    val args: TeamEditDestination = backStackEntry.toRoute()
    val viewModel: TeamEditViewModel = koinViewModel {
        parametersOf(navController.getDestination<TournamentDetailDestination>().id.toObjectId())
    }

    LaunchedEffect(viewModel) {
        if (args.id == null) {
            viewModel.loadCreate()
        } else {
            viewModel.loadEdit(args.id.toObjectId())
        }
    }

    TeamEditScreen(
        navController = navController,
        state = viewModel.state,
        onSaveButtonClick = {
            viewModel.saveChanges {
                navController.popBackStack()
            }
        },
        isEditMode = args.id != null
    )
}