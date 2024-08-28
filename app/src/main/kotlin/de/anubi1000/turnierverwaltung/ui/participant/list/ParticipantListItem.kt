package de.anubi1000.turnierverwaltung.ui.participant.list

import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import de.anubi1000.turnierverwaltung.database.model.Participant

@Composable
fun ParticipantListItem(
    participant: Participant,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(text = participant.name)
        },
        colors = ListItemDefaults.colors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .selectable(selected = selected, onClick = onClick)
    )
}