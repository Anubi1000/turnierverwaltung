package de.anubi1000.turnierverwaltung.ui.participant.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.participant.EditParticipant

@Composable
fun RowScope.GeneralCard(
    participant: EditParticipant
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.weight(weight = 0.4f, fill = true)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = LocalStrings.current.general, style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = participant.name,
                onValueChange = {
                    participant.name = it.replace("\n", "")
                },
                label = { Text(text = LocalStrings.current.name) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                singleLine = true,
                isError = participant.name.isBlank()
            )
        }
    }

    Box(modifier = Modifier.weight(weight = 0.6f, fill = true))
}