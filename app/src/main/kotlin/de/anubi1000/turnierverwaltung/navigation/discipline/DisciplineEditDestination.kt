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
import de.anubi1000.turnierverwaltung.ui.util.getDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.discipline.DisciplineEditViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.mongodb.kbson.ObjectId

@Serializable
data class DisciplineEditDestination(val id: String?) : AppDestination {
    constructor(id: ObjectId? = null) : this(id?.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.DISCIPLINES
}

fun NavGraphBuilder.disciplineEditDestination(
    navController: NavController,
) = composable<DisciplineEditDestination> { backStackEntry ->
    val args: DisciplineEditDestination = backStackEntry.toRoute()
    val viewModel: DisciplineEditViewModel = koinViewModel {
        val destination = navController.getDestination<TournamentDetailDestination>()
        parametersOf(destination.id.toObjectId())
    }

    LaunchedEffect(viewModel) {
        if (args.id == null) {
            viewModel.loadCreate()
        } else {
            viewModel.loadEdit(args.id.toObjectId())
        }
    }

    DisciplineEditScreen(
        navController = navController,
        state = viewModel.state,
        onSaveButtonClick = {
            viewModel.saveChanges {
                navController.popBackStack()
            }
        },
        isEditMode = args.id != null,
    )
}
