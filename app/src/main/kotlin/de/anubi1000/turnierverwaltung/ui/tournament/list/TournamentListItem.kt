package de.anubi1000.turnierverwaltung.ui.tournament.list

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.ui.util.SelectableListItem
import de.anubi1000.turnierverwaltung.util.formatAsDate

@Composable
fun TournamentListItem(
    tournament: Tournament,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    SelectableListItem(
        headlineContent = { Text(text = tournament.name) },
        supportingContent = { Text(text = tournament.date.formatAsDate()) },
        selected = selected,
        onClick = onClick,
        modifier = modifier
    )
}