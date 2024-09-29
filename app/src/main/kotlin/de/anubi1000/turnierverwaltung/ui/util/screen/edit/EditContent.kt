@file:Suppress("MatchingDeclarationName")

package de.anubi1000.turnierverwaltung.ui.util.screen.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@OptIn(ExperimentalLayoutApi::class)
class EditContentScope(val flowRowScope: FlowRowScope)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditContent(
    modifier: Modifier = Modifier,
    content: @Composable EditContentScope.() -> Unit,
) {
    val arrangement = Arrangement.spacedBy(4.dp)
    FlowRow(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalArrangement = arrangement,
        verticalArrangement = arrangement,
        content = {
            val scope = remember(this) { EditContentScope(this) }
            scope.content()
        },
    )
}
