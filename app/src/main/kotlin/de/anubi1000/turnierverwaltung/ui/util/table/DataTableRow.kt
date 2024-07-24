package de.anubi1000.turnierverwaltung.ui.util.table

import androidx.compose.runtime.Composable
import kotlinx.collections.immutable.ImmutableList

data class DataTableRow(
    val items: ImmutableList<Item>,
) {
    data class Item(
        val content: @Composable () -> Unit
    )
}
