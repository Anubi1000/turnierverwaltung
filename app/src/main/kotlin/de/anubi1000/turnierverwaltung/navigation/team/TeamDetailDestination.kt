package de.anubi1000.turnierverwaltung.navigation.team

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.participant.detail.ParticipantDetailScreen
import de.anubi1000.turnierverwaltung.ui.shared.TournamentNavigationLayout
import de.anubi1000.turnierverwaltung.ui.shared.list.ParticipantListLayout
import de.anubi1000.turnierverwaltung.ui.shared.list.TeamListLayout
import de.anubi1000.turnierverwaltung.ui.team.detail.TeamDetailScreen
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.participant.ParticipantDetailViewModel
import de.anubi1000.turnierverwaltung.viewmodel.team.TeamDetailViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.mongodb.kbson.ObjectId

@Serializable
data class TeamDetailDestination(val id: String) : AppDestination {
    constructor(id: ObjectId) : this(id.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.TEAMS
}

fun NavGraphBuilder.teamDetailDestination(navController: NavController) = composable<TeamDetailDestination> { backStackEntry ->
    val args: TeamDetailDestination = backStackEntry.toRoute()

    TournamentNavigationLayout(navController) {
        TeamListLayout(navController) {
            val viewModel: TeamDetailViewModel = koinViewModel()

            LaunchedEffect(viewModel) {
                viewModel.loadItem(args.id.toObjectId())
            }

            TeamDetailScreen(
                navController = navController,
                state = viewModel.state,
                onDeleteButtonClick = {
                    viewModel.deleteItem()
                }
            )
        }
    }
}