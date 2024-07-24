package de.anubi1000.turnierverwaltung.navigation.old

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.kodein.rememberScreenModel
import de.anubi1000.turnierverwaltung.ui.participant.edit.ParticipantEditScreen
import de.anubi1000.turnierverwaltung.viewmodel.ParticipantEditViewModel
import org.mongodb.kbson.ObjectId

class ParticipantCreateDestination(val tournamentId: ObjectId) : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel<ParticipantEditViewModel>()

        LaunchedEffect(Unit) {
            viewModel.loadCreate(tournamentId = tournamentId)
        }

        val state by viewModel.state.collectAsState()

        ParticipantEditScreen(
            state = state,
            editMode = viewModel.editMode
        )
    }
}