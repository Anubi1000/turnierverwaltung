package de.anubi1000.turnierverwaltung.navigation.participant

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.ui.participant.detail.ParticipantDetailScreen
import de.anubi1000.turnierverwaltung.ui.participant.edit.ParticipantEditScreen
import de.anubi1000.turnierverwaltung.ui.shared.TournamentNavigationLayout
import de.anubi1000.turnierverwaltung.ui.shared.list.ParticipantListLayout
import de.anubi1000.turnierverwaltung.util.getDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.participant.ParticipantDetailViewModel
import de.anubi1000.turnierverwaltung.viewmodel.participant.ParticipantEditViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.mongodb.kbson.ObjectId

@Serializable
data class ParticipantDetailDestination(val participantId: String) : AppDestination {
    constructor(participantId: ObjectId) : this(participantId.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.PARTICIPANTS
}

fun NavGraphBuilder.participantDetailDestination(navController: NavController) = composable<ParticipantDetailDestination> { backStackEntry ->
    val args: ParticipantDetailDestination = backStackEntry.toRoute()

    TournamentNavigationLayout(navController) {
        ParticipantListLayout(navController) {
            val viewModel: ParticipantDetailViewModel = koinViewModel()

            LaunchedEffect(viewModel) {
                viewModel.loadItem(args.participantId.toObjectId())
            }

            ParticipantDetailScreen(
                navController = navController,
                state = viewModel.state,
                onDeleteButtonClick = {
                    viewModel.deleteItem()
                }
            )
        }
    }
}