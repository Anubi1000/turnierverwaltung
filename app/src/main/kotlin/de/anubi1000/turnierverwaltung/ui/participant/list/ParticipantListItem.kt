package de.anubi1000.turnierverwaltung.ui.participant.list

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.participant.ListParticipant
import de.anubi1000.turnierverwaltung.ui.theme.AppTheme

@Composable
fun ParticipantListItem(
    participant: ListParticipant,
    shouldColor: Boolean = false,
    onDeleteButtonClick: () -> Unit = {}
) {
    ListItem(
        headlineContent = {
            Text(text = participant.name)
        },
        supportingContent = {
            Text(text = participant.verein)
        },
        trailingContent = {
            Box {
                var expanded by remember { mutableStateOf(false) }

                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, null)
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text(LocalStrings.current.edit) },
                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                        onClick = {  }
                    )
                    DropdownMenuItem(
                        text = { Text(LocalStrings.current.delete) },
                        leadingIcon = { Icon(Icons.Default.Delete, null) },
                        onClick = {
                            expanded = false
                            onDeleteButtonClick()
                        }
                    )
                }
            }

        },
        colors = ListItemDefaults.colors(
            containerColor = if (shouldColor) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.background
        ),
        modifier = Modifier.clip(MaterialTheme.shapes.medium)
    )
}

@Preview
@Composable
private fun ParticipantListItemPreviewDefault() {
    AppTheme {
        ParticipantListItem(
            participant = ListParticipant(
                name = "Teilnehmer",
                verein = "Verein"
            )
        )
    }
}

@Preview
@Composable
private fun ParticipantListItemPreviewColored() {
    AppTheme {
        ParticipantListItem(
            participant = ListParticipant(
                name = "Teilnehmer",
                verein = "Verein"
            ),
            shouldColor = true
        )
    }
}