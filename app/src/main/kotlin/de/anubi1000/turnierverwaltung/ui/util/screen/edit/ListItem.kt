package de.anubi1000.turnierverwaltung.ui.util.screen.edit

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.ListItem as Material3ListItem

@Suppress("UnusedReceiverParameter")
@Composable
fun EditCardScope.ListItem(
    headlineContent: @Composable () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Material3ListItem(
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    )
}
