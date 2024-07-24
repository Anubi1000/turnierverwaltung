package de.anubi1000.turnierverwaltung.ui.util.table

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@Composable
fun DataTable(
    columns: ImmutableList<DataTableColumn>,
    rows: ImmutableList<DataTableRow>
) {
    LazyColumn {

    }
    Column {
        Row(
            modifier = Modifier.padding(vertical = 6.dp).heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            columns.forEach { column ->
                val widthModifier = when (column.width) {
                    is ColumnWidth.Weight -> Modifier.weight(column.width.weight, fill = true)
                    is ColumnWidth.Fixed -> Modifier.width(column.width.width)
                }
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp).then(widthModifier),
                    contentAlignment = when (column.alignment) {
                        ColumnAlignment.START -> Alignment.CenterStart
                        ColumnAlignment.CENTER -> Alignment.Center
                        ColumnAlignment.END -> Alignment.CenterEnd
                    }
                ) {
                    val textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
                    CompositionLocalProvider(LocalTextStyle provides textStyle) {
                        column.header()
                    }
                }
            }
        }

        rows.forEach { row ->
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline
            )

            Row(
                modifier = Modifier.padding(vertical = 6.dp).heightIn(min = 52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.items.forEachIndexed { index, rowContent ->
                    val column = columns[index]
                    val widthModifier = when (column.width) {
                        is ColumnWidth.Weight -> Modifier.weight(column.width.weight, fill = true)
                        is ColumnWidth.Fixed -> Modifier.width(column.width.width)
                    }
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp).then(widthModifier),
                        contentAlignment = when (column.alignment) {
                            ColumnAlignment.START -> Alignment.CenterStart
                            ColumnAlignment.CENTER -> Alignment.Center
                            ColumnAlignment.END -> Alignment.CenterEnd
                        }
                    ) {
                        rowContent.content()
                    }
                }
            }
        }
    }
}
