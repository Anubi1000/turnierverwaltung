package de.anubi1000.turnierverwaltung.ui.util.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailContent(
    modifier: Modifier = Modifier,
    content: @Composable FlowRowScope.() -> Unit
) {
    val arrangement = Arrangement.spacedBy(4.dp)
    FlowRow(
        modifier = modifier,
        horizontalArrangement = arrangement,
        verticalArrangement = arrangement,
        content = content
    )
}
