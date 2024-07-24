package de.anubi1000.turnierverwaltung.ui.tournament.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.anubi1000.turnierverwaltung.viewmodel.TournamentDetailViewModel

@Composable
fun LoadedContent(
    state: TournamentDetailViewModel.State.Loaded,
) {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        GeneralCard(
            state = state,
            modifier = Modifier.weight(0.4f, fill = true),
        )

        Spacer(Modifier.width(8.dp))

        ValuesCard(
            state = state,
            modifier = Modifier.weight(0.6f, fill = true),
        )
    }
}
