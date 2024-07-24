package de.anubi1000.turnierverwaltung.navigation.old

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import de.anubi1000.turnierverwaltung.ui.participant.list.ParticipantListScreen
import de.anubi1000.turnierverwaltung.viewmodel.ParticipantListViewModel
import org.mongodb.kbson.ObjectId

data class ParticipantListDestination(val tournamentId: ObjectId) : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel<ParticipantListViewModel>()

        LaunchedEffect(viewModel) {
            viewModel.loadParticipants(tournamentId)
        }

        val state by viewModel.state.collectAsState()

        ParticipantListScreen(
            state = state,
            tournamentId = tournamentId,
            deleteParticipant = {
                viewModel.deleteParticipant(it.id)
            }
        )
    }
}