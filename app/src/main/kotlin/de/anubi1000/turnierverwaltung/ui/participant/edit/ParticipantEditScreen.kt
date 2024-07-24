package de.anubi1000.turnierverwaltung.ui.participant.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.viewmodel.ParticipantEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantEditScreen(state: ParticipantEditViewModel.State, editMode: Boolean) {
    val navigator = LocalNavigator.currentOrThrow

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (editMode) LocalStrings.current.editParticipant else LocalStrings.current.createParticipant)
                        },
                navigationIcon = {
                    IconButton(
                        onClick = { navigator.pop() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        when (state) {
            is ParticipantEditViewModel.State.Loading -> LoadingIndicator(
                modifier = Modifier.padding(it)
            )
            is ParticipantEditViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = Modifier.padding(it)
            )
        }
    }
}