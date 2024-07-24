package de.anubi1000.turnierverwaltung.ui.tournament.list

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.anubi1000.turnierverwaltung.data.tournament.ListTournament
import de.anubi1000.turnierverwaltung.ui.theme.AppTheme
import de.anubi1000.turnierverwaltung.util.formatAsDate

@Composable
fun TournamentListItem(
    tournament: ListTournament,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    ListItem(
        headlineContent = {
            Text(tournament.name)
        },
        supportingContent = {
            Text(tournament.date.formatAsDate())
        },
        colors = ListItemDefaults.colors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier
            .padding(vertical = 2.dp)
            .clip(MaterialTheme.shapes.medium)
            .selectable(selected = selected, onClick = onClick)
    )
}

@Preview
@Composable
private fun TournamentListItemPreview() {
    AppTheme {
        TournamentListItem(
            tournament = ListTournament(
                name = "Turnier"
            )
        )
    }
}
