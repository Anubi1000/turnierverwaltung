package de.anubi1000.turnierverwaltung.navigation.discipline

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.ui.discipline.DisciplineEditScreen
import de.anubi1000.turnierverwaltung.ui.discipline.TeamDisciplineEditScreen
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.discipline.DisciplineEditViewModel
import de.anubi1000.turnierverwaltung.viewmodel.discipline.TeamDisciplineEditViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.mongodb.kbson.ObjectId

@Serializable
data class TeamDisciplineEditDestination(
    val id: String?
) : AppDestination {
    constructor(id: ObjectId? = null) : this(id?.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.DISCIPLINES
}

fun NavGraphBuilder.teamDisciplineEditDestination(navController: NavController) = composable<TeamDisciplineEditDestination> { backStackEntry ->
    val args: TeamDisciplineEditDestination = backStackEntry.toRoute()
    val viewModel: TeamDisciplineEditViewModel = koinViewModel {
        val destination: TournamentDetailDestination = navController.getBackStackEntry<TournamentDetailDestination>().toRoute()
        parametersOf(destination.id.toObjectId())
    }

    LaunchedEffect(viewModel) {
        if (args.id == null) {
            viewModel.loadCreate()
        } else {
            viewModel.loadEdit(args.id.toObjectId())
        }
    }

    TeamDisciplineEditScreen(
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
