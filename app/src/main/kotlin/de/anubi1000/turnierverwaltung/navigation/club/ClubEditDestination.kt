package de.anubi1000.turnierverwaltung.navigation.club

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.ui.club.ClubEditScreen
import de.anubi1000.turnierverwaltung.util.getDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.club.ClubEditViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.mongodb.kbson.ObjectId

@Serializable
data class ClubEditDestination(
    val id: String?
) : AppDestination {
    constructor(id: ObjectId? = null) : this(id?.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.CLUBS
}

fun NavGraphBuilder.clubEditDestination(navController: NavController) = composable<ClubEditDestination> { backStackEntry ->
    val args: ClubEditDestination = backStackEntry.toRoute()
    val viewModel: ClubEditViewModel = koinViewModel {
        parametersOf(navController.getDestination<TournamentDetailDestination>().id.toObjectId())
    }

    LaunchedEffect(viewModel) {
        if (args.id == null) {
            viewModel.loadCreate()
        } else {
            viewModel.loadEdit(args.id.toObjectId())
        }
    }

    ClubEditScreen(
        navController = navController,
        state = viewModel.state,
        onSaveButtonClick = {
            viewModel.saveChanges {
                navController.navigate(ClubDetailDestination(it)) {
                    popUpTo<ClubListDestination>()
                }
            }
        },
        isEditMode = args.id != null
    )
}
