package de.anubi1000.turnierverwaltung.ui.util.screen.detail

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.anubi1000.turnierverwaltung.ui.theme.AppTheme

@Composable
fun DetailCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)
        )

        content()
    }
}

@Composable
@Preview
private fun DetailCardPreview() {
    AppTheme {
        DetailCard(
            title = "Title"
        ) {
            repeat(4) { index ->
                DetailItem(
                    headlineText = "Headline $index",
                    overlineText = "Overline $index"
                )
            }
        }
    }
}
