package de.anubi1000.turnierverwaltung.ui.util.screen.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class EditContentScope
@OptIn(ExperimentalLayoutApi::class)
constructor(
    val flowRowScope: FlowRowScope,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditContent(
    modifier: Modifier = Modifier,
    content: @Composable EditContentScope.() -> Unit,
) {
    val arrangement = Arrangement.spacedBy(4.dp)
    FlowRow(
        modifier = modifier,
        horizontalArrangement = arrangement,
        verticalArrangement = arrangement,
        content = {
            EditContentScope(this).content()
        },
    )
}
