package de.anubi1000.turnierverwaltung.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp

@Composable
fun ListDetailLayout(
    listContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val availableWidth = maxWidth - 8.dp
        val firstWidth = (availableWidth * 0.4f).coerceIn(300.dp, 420.dp)
        val secondWidth = availableWidth - firstWidth

        Row {
            Box(
                modifier = Modifier.width(firstWidth)
            ) {
                listContent()
            }

            Box(
                modifier = Modifier.width(secondWidth)
            ) {
                detailContent()
            }
        }
    }
}