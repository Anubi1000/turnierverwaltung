package de.anubi1000.turnierverwaltung.ui.util.screen.edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
@Composable
fun EditCardScope.TextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
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
        readOnly = readOnly,
        singleLine = singleLine,
        textStyle = if (readOnly) {
            LocalTextStyle.current.merge(
                color = Color.Gray,
            )
        } else {
            LocalTextStyle.current
        },
        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp).fillMaxWidth(),
    )
}
