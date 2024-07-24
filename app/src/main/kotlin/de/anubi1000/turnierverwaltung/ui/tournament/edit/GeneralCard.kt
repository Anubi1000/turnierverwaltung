package de.anubi1000.turnierverwaltung.ui.tournament.edit

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.tournament.EditTournament
import de.anubi1000.turnierverwaltung.ui.theme.AppTheme
import de.anubi1000.turnierverwaltung.ui.util.CardWithTitle
import de.anubi1000.turnierverwaltung.util.formatAsDate
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralCard(
    tournament: EditTournament,
    modifier: Modifier = Modifier
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }

    CardWithTitle(
        title = LocalStrings.current.general,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        val textFieldModifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 16.dp, end = 16.dp)

        OutlinedTextField(
            value = tournament.name,
            onValueChange = {
                tournament.name = it.replace("\n", "")
            },
            label = { Text(text = LocalStrings.current.name) },
            modifier = textFieldModifier,
            singleLine = true,
            isError = tournament.name.isBlank()
        )

        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Release) {
                    showDatePickerDialog = true
                }
            }
        }

        OutlinedTextField(
            value = tournament.date.formatAsDate(),
            onValueChange = {},
            label = { Text(LocalStrings.current.dateOfTournament) },
            modifier = textFieldModifier.padding(bottom = 8.dp),
            singleLine = true,
            readOnly = true,
            interactionSource = interactionSource
        )
    }

    if (showDatePickerDialog) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = tournament.date.toEpochMilli(),
        )

        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { date ->
                            showDatePickerDialog = false
                            tournament.date = Instant.ofEpochMilli(date)
                        }
                    }
                ) {
                    Text(text = LocalStrings.current.confirm)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePickerDialog = false }
                ) {
                    Text(text = LocalStrings.current.cancel)
                }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@Preview
@Composable
fun GeneralCardPreview() {
    AppTheme {
        Row {
            val tournament = remember { EditTournament() }

            GeneralCard(
                tournament = tournament
            )
        }
    }
}