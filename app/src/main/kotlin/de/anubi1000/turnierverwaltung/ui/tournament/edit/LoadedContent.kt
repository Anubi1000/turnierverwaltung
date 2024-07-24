package de.anubi1000.turnierverwaltung.ui.tournament.edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.anubi1000.turnierverwaltung.viewmodel.TournamentEditViewModel

@Composable
fun LoadedContent(
    state: TournamentEditViewModel.State.Loaded,
    isEditMode: Boolean
) {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        GeneralCard(
            tournament = state.tournament,
            modifier = Modifier.weight(weight = 0.4f, fill = true)
        )

        Spacer(Modifier.width(8.dp))

        ValuesCard(
            tournament = state.tournament,
            modifier = Modifier.weight(weight = 0.6F, fill = true),
            isEditMode = isEditMode
        )
    }
}