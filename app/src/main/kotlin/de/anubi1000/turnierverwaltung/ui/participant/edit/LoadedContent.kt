package de.anubi1000.turnierverwaltung.ui.participant.edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.anubi1000.turnierverwaltung.viewmodel.ParticipantEditViewModel

@Composable
fun LoadedContent(
    state: ParticipantEditViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    Row (
        modifier = modifier.padding(horizontal = 16.dp).fillMaxSize()
    ) {
        GeneralCard(
            participant = state.participant
        )
    }
}