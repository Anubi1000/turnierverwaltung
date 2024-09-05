package de.anubi1000.turnierverwaltung.navigation.participant

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.participant.ParticipantResultScreen
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.discipline.ParticipantResultViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.mongodb.kbson.ObjectId

@Serializable
data class ParticipantResultDestination(
    val participantId: String,
    val disciplineId: String
) : AppDestination {
    constructor(participantId: ObjectId, disciplineId: ObjectId) : this(participantId.toHexString(), disciplineId.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.PARTICIPANTS
}

fun NavGraphBuilder.participantResultDestination(navController: NavController) = composable<ParticipantResultDestination> { backStackEntry ->
    val args: ParticipantResultDestination = backStackEntry.toRoute()
    val viewModel: ParticipantResultViewModel = koinViewModel()

    LaunchedEffect(viewModel) {
        viewModel.loadResults(args.participantId.toObjectId(), args.disciplineId.toObjectId())
    }

    ParticipantResultScreen(
        navController = navController,
        state = viewModel.state,
        onSaveButtonClick = {
            viewModel.saveChanges {
                navController.popBackStack()
            }
        }
    )
}