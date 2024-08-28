package de.anubi1000.turnierverwaltung.ui.util.screen.detail

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.anubi1000.turnierverwaltung.ui.theme.AppTheme

@Composable
fun DetailItem(
    headlineText: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    overlineText: String? = null
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
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = modifier
    )
}

@Composable
@Preview
private fun DetailItemPreview() {
    AppTheme {
        DetailItem(
            headlineText = "Headline",
            supportingText = "Supporting",
            overlineText = "Overline"
        )
    }
}
