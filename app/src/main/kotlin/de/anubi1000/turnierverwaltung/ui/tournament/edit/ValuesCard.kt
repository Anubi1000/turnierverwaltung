package de.anubi1000.turnierverwaltung.ui.tournament.edit

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.tournament.EditTournament
import de.anubi1000.turnierverwaltung.ui.theme.AppTheme
import de.anubi1000.turnierverwaltung.ui.util.CardWithTitle
import de.anubi1000.turnierverwaltung.ui.util.table.ColumnAlignment
import de.anubi1000.turnierverwaltung.ui.util.table.ColumnWidth
import de.anubi1000.turnierverwaltung.ui.util.table.DataTable
import de.anubi1000.turnierverwaltung.ui.util.table.DataTableColumn
import de.anubi1000.turnierverwaltung.ui.util.table.DataTableRow
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.ValuesCard(
    tournament: EditTournament,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false
) {
    val tableColumns = remember(isEditMode) {
        buildList {
            add(DataTableColumn(
                header = { Text(LocalStrings.current.name) },
                alignment = ColumnAlignment.CENTER
            ))
            add(DataTableColumn(
                header = { Text(LocalStrings.current.subtract) },
                alignment = ColumnAlignment.CENTER,
                width = ColumnWidth.Fixed(150.dp)
            ))
            if (!isEditMode) {
                add(DataTableColumn(
                    header = { Text(LocalStrings.current.delete) },
                    alignment = ColumnAlignment.CENTER,
                    width = ColumnWidth.Fixed(100.dp)
                ))
            }
        }.toPersistentList()
    }

    CardWithTitle(
        title = LocalStrings.current.values,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        actions = {
            if (!isEditMode) {
                TextButton(
                    onClick = {
                        tournament.values.add(EditTournament.Value())
                    }
                ) {
                    Icon(Icons.Default.Add, null)
                    Text(text = LocalStrings.current.newValue)
                }
            }
        }
    ) {
       val rows by remember(tournament) {
            derivedStateOf {
                tournament.values.map { value ->
                    buildRow(
                        value = value,
                        isEditMode = isEditMode,
                        onDeleteButtonClick = {
                            tournament.values.remove(value)
                        }
                    )
                }.toPersistentList()
            }
        }

        DataTable(
            columns = tableColumns,
            rows = rows
        )
    }
}

private fun buildRow(
    value: EditTournament.Value,
    isEditMode: Boolean = false,
    onDeleteButtonClick: () -> Unit
): DataTableRow {
    val items = mutableListOf<DataTableRow.Item>()

    items.add(DataTableRow.Item {
        OutlinedTextField(
            value = value.name,
            onValueChange = {
                value.name = it.replace("\n", "")
            },
            modifier = Modifier.fillMaxWidth(),
            isError = value.name.isBlank()
        )
    })

    items.add(DataTableRow.Item {
        Checkbox(
            checked = value.subtract,
            onCheckedChange = {
                value.subtract = it
            }
        )
    })

    if (!isEditMode) {
        items.add(DataTableRow.Item {
            IconButton(
                onClick = onDeleteButtonClick
            ) {
                Icon(Icons.Default.Delete, null)
            }
        })
    }

    return DataTableRow(
        items = items.toPersistentList()
    )
}

@Preview
@Composable
fun ValuesCardPreview() {
    AppTheme {
        Row {
            val tournament = remember { EditTournament() }

            ValuesCard(
                tournament = tournament
            )
        }
    }
}