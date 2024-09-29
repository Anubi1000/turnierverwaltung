package de.anubi1000.turnierverwaltung.navigation.participant

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.participant.ParticipantDetailScreen
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.participant.ParticipantDetailViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.mongodb.kbson.ObjectId

@Serializable
data class ParticipantDetailDestination(val id: String) : AppDestination {
    constructor(id: ObjectId) : this(id.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.PARTICIPANTS
}

fun NavGraphBuilder.participantDetailDestination(
    navController: NavController,
) = composable<ParticipantDetailDestination> { backStackEntry ->
    val args: ParticipantDetailDestination = backStackEntry.toRoute()
    val viewModel: ParticipantDetailViewModel = koinViewModel()

    LaunchedEffect(viewModel) {
        viewModel.loadItem(args.id.toObjectId())
    }

    ParticipantDetailScreen(
        navController = navController,
        state = viewModel.state,
        onDeleteButtonClick = {
            viewModel.deleteItem {
                navController.popBackStack()
            }
        },
    )
}
