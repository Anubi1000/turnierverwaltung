package de.anubi1000.turnierverwaltung.ui.util.screen.edit

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
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
import de.anubi1000.turnierverwaltung.util.formatAsDate
import java.time.Instant

@Suppress("UnusedReceiverParameter")
@Composable
fun EditCardScope.TextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (singleLine) {
                val newValue = it.replace("\n", "")
                if (newValue != value) onValueChange(newValue)
            } else {
                onValueChange(it)
            }
        },
        label = { Text(label) },
        singleLine = singleLine,
        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp).fillMaxWidth(),
    )
}

@Suppress("UnusedReceiverParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCardScope.DateField(
    date: Instant,
    onDateChange: (Instant) -> Unit,
    label: String,
) {
    var showDateDialog by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showDateDialog = true
            }
        }
    }

    OutlinedTextField(
        value = date.formatAsDate(),
        onValueChange = {},
        label = { Text(label) },
        interactionSource = interactionSource,
        readOnly = true,
        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp).fillMaxWidth(),
    )

    if (showDateDialog) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = date.toEpochMilli(),
        )

        DatePickerDialog(
            onDismissRequest = { showDateDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { date ->
                            showDateDialog = false
                            onDateChange(Instant.ofEpochMilli(date))
                        }
                    },
                ) {
                    Text(text = LocalStrings.current.confirm)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDateDialog = false },
                ) {
                    Text(text = LocalStrings.current.cancel)
                }
            },
        ) {
            DatePicker(state = state)
        }
    }
}

@Suppress("UnusedReceiverParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCardScope.DropdownMenu(
    value: String,
    label: String,
    content: @Composable ColumnScope.(close: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            content = {
                content {
                    expanded = false
                }
            },
        )
    }
}
