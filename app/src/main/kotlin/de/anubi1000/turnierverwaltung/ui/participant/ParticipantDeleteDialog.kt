package de.anubi1000.turnierverwaltung.ui.participant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import cafe.adriel.lyricist.LocalStrings

@Composable
fun ParticipantDeleteDialog(
    participantName: String,
    onDismissRequest: () -> Unit,
    onConfirmButtonClicked: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmButtonClicked) {
                Text(LocalStrings.current.delete)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(LocalStrings.current.cancel)
            }
        },
        icon = { Icon(Icons.Outlined.DeleteForever, null) },
        title = { Text(LocalStrings.current.deleteParticipant) },
        text = {
            Text(LocalStrings.current.wantToDeleteParticipant(participantName))
        },
    )
}