package de.anubi1000.turnierverwaltung.ui.util.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class DetailContentScope
@OptIn(ExperimentalLayoutApi::class)
constructor(
    val flowRowScope: FlowRowScope,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailContent(
    modifier: Modifier = Modifier,
    content: @Composable DetailContentScope.() -> Unit,
) {
    val arrangement = Arrangement.spacedBy(4.dp)
    FlowRow(
        modifier = modifier,
        horizontalArrangement = arrangement,
        verticalArrangement = arrangement,
        content = {
            DetailContentScope(this).content()
        },
    )
}
