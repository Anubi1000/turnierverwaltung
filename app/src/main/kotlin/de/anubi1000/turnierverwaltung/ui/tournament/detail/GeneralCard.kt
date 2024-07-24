package de.anubi1000.turnierverwaltung.ui.tournament.detail

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.CardWithTitle
import de.anubi1000.turnierverwaltung.util.formatAsDate
import de.anubi1000.turnierverwaltung.viewmodel.TournamentDetailViewModel

@Composable
fun GeneralCard(state: TournamentDetailViewModel.State.Loaded, modifier: Modifier = Modifier) {
    CardWithTitle(
        title = LocalStrings.current.general,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        val itemColors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )

        ListItem(
            headlineContent = { Text(state.tournament.name) },
            overlineContent = { Text(LocalStrings.current.name) },
            colors = itemColors
        )
        ListItem(
            headlineContent = { Text(state.tournament.date.formatAsDate()) },
            overlineContent = { Text(LocalStrings.current.dateOfTournament) },
            colors = itemColors
        )
    }
}