package de.anubi1000.turnierverwaltung.navigation.club

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.club.edit.ClubEditScreen
import de.anubi1000.turnierverwaltung.ui.shared.TournamentNavigationLayout
import de.anubi1000.turnierverwaltung.ui.shared.list.ClubListLayout
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.club.ClubEditViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data class ClubEditDestination(
    val clubId: String?
) : AppDestination {
    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.CLUBS
}

fun NavGraphBuilder.clubEditDestination(navController: NavController) = composable<ClubEditDestination> { backStackEntry ->
    val args: ClubEditDestination = backStackEntry.toRoute()

    TournamentNavigationLayout(navController) {
        ClubListLayout(navController) {
            val viewModel: ClubEditViewModel = koinViewModel()

            LaunchedEffect(viewModel) {
                if (args.clubId == null) {
                    viewModel.loadCreate()
                } else {
                    viewModel.loadEdit(args.clubId.toObjectId())
                }
            }

            ClubEditScreen(
                navController = navController,
                state = viewModel.state,
                onSaveButtonClick = {
                    viewModel.saveChanges {
                        navController.navigate(ClubListDestination) {
                            popUpTo<ClubListDestination>()
                        }
                    }
                },
                isEditMode = args.clubId != null
            )
        }
    }
}
