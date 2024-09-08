package de.anubi1000.turnierverwaltung.ui.util.screen.detail

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class DetailCardScope

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailContentScope.DetailCard(
    title: String,
    width: Dp = 450.dp,
    content: @Composable DetailCardScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        modifier = with(flowRowScope) {
            Modifier.fillMaxRowHeight().width(width)
        },
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
        )

        DetailCardScope().content()
    }
}
