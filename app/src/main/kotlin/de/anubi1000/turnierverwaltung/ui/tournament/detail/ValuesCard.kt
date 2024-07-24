package de.anubi1000.turnierverwaltung.ui.tournament.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.CardWithTitle
import de.anubi1000.turnierverwaltung.viewmodel.TournamentDetailViewModel

@Composable
fun ValuesCard(state: TournamentDetailViewModel.State.Loaded, modifier: Modifier = Modifier) {
    CardWithTitle(
        title = LocalStrings.current.values,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        val itemColors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )

        state.tournament.values.forEach { value ->
            Row {
                val itemModifier = Modifier.weight(0.5f, fill = true)

                ListItem(
                    headlineContent = { Text(value.name) },
                    overlineContent = { Text(LocalStrings.current.name) },
                    colors = itemColors,
                    modifier = itemModifier
                )

                ListItem(
                    headlineContent = { Text(LocalStrings.current.yesno(value.subtract)) },
                    overlineContent = { Text(LocalStrings.current.isSubtracted) },
                    colors = itemColors,
                    modifier = itemModifier
                )
            }
        }
    }
}