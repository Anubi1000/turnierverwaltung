package de.anubi1000.turnierverwaltung.navigation.participant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentEditDestination
import de.anubi1000.turnierverwaltung.ui.participant.list.ParticipantEditScreen
import de.anubi1000.turnierverwaltung.ui.shared.TournamentNavigationLayout
import de.anubi1000.turnierverwaltung.ui.shared.list.ParticipantListLayout
import de.anubi1000.turnierverwaltung.util.getDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.util.topAppBarPadding
import de.anubi1000.turnierverwaltung.viewmodel.club.ClubEditViewModel
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

    TournamentNavigationLayout(navController) {
        ParticipantListLayout(navController) {
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
    }
}