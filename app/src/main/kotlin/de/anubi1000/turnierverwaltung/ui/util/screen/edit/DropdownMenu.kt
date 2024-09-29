@file:Suppress("MatchingDeclarationName")

package de.anubi1000.turnierverwaltung.ui.util.screen.edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.anubi1000.turnierverwaltung.ui.util.Icon
import androidx.compose.material3.DropdownMenuItem as Material3DropdownMenuItem

class DropdownMenuScope(val close: () -> Unit)

@Suppress("UnusedReceiverParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCardScope.DropdownMenu(
    value: String,
    label: String,
    content: @Composable DropdownMenuScope.() -> Unit,
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { setExpanded(it) },
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
            onDismissRequest = { setExpanded(false) },
            content = {
                val scope = remember(setExpanded) {
                    DropdownMenuScope {
                        setExpanded(false)
                    }
                }
                scope.content()
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuScope.DropdownMenuItem(
    text: String,
    onClick: () -> Unit,
    selected: Boolean? = null,
) {
    Material3DropdownMenuItem(
        text = {
            Text(text)
        },
        onClick = {
            close()
            onClick()
        },
        leadingIcon = if (selected != null) {
            {
                if (selected) {
                    Icon(Icons.Default.Check)
                }
            }
        } else {
            null
        },
        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
    )
}
