package de.anubi1000.turnierverwaltung.navigation.participant

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.ui.participant.edit.ParticipantEditScreen
import de.anubi1000.turnierverwaltung.util.getDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.participant.ParticipantEditViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.mongodb.kbson.ObjectId

@Serializable
data class ParticipantEditDestination(val participantId: String?) : AppDestination {
    constructor(participantId: ObjectId? = null) : this(participantId?.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.PARTICIPANTS
}

fun NavGraphBuilder.participantEditDestination(navController: NavController) = composable<ParticipantEditDestination> { backStackEntry ->
    val args: ParticipantEditDestination = backStackEntry.toRoute()
    val viewModel: ParticipantEditViewModel = koinViewModel {
        parametersOf(navController.getDestination<TournamentDetailDestination>().tournamentId.toObjectId())
    }

    LaunchedEffect(viewModel) {
        if (args.participantId == null) {
            viewModel.loadCreate()
        } else {
            viewModel.loadEdit(args.participantId.toObjectId())
        }
    }

    ParticipantEditScreen(
        navController = navController,
        state = viewModel.state,
        onSaveButtonClick = {
            viewModel.saveChanges {
                navController.popBackStack()
            }
        },
        isEditMode = args.participantId != null
    )
}