package de.anubi1000.turnierverwaltung.ui.util.table

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

data class DataTableColumn(
    val header: @Composable () -> Unit,
    val width: ColumnWidth = ColumnWidth.Weight(1f),
    val alignment: ColumnAlignment = ColumnAlignment.START
)

sealed interface ColumnWidth {
    data class Weight(val weight: Float) : ColumnWidth
    data class Fixed(val width: Dp) : ColumnWidth
}

enum class ColumnAlignment {
    START,
    CENTER,
    END
}