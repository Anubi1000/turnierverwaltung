package de.anubi1000.turnierverwaltung.navigation.club

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.club.detail.ClubDetailScreen
import de.anubi1000.turnierverwaltung.ui.shared.TopLevelNavigationLayout
import de.anubi1000.turnierverwaltung.ui.shared.list.ClubListLayout
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.club.ClubDetailViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.mongodb.kbson.ObjectId

@Serializable
data class ClubDetailDestination(
    val id: String
) : AppDestination {
    constructor(id: ObjectId) : this(id.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.TOURNAMENTS
}

fun NavGraphBuilder.clubDetailDestinations(navController: NavController) = composable<ClubDetailDestination> { backStackEntry ->
    val args = backStackEntry.toRoute<ClubDetailDestination>()

    TopLevelNavigationLayout(navController) {
        ClubListLayout(navController) {
            val viewModel: ClubDetailViewModel = koinViewModel()

            LaunchedEffect(viewModel) {
                viewModel.loadItem(args.id.toObjectId())
            }

            ClubDetailScreen(
                navController = navController,
                state = viewModel.state,
                onDeleteButtonClick = {
                    viewModel.deleteItem()
                }
            )
        }
    }
}
