package de.anubi1000.turnierverwaltung.ui.util.screen.detail

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DetailCardScope.DetailItem(
    headlineText: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    overlineText: String? = null,
) {
    ListItem(
        headlineContent = {
            Text(headlineText)
        },
        supportingContent = supportingText?.let {
            {
                Text(it)
            }
        },
        overlineContent = overlineText?.let {
            {
                Text(it)
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        modifier = modifier,
    )
}
