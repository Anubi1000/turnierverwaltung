package de.anubi1000.turnierverwaltung.ui.util.screen.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton

@Suppress("UnusedReceiverParameter")
@Composable
fun <T> EditContentScope.SelectCard(
    title: String,
    selectedItems: SnapshotStateList<T>,
    allItems: List<T>,
    itemName: (T) -> String,
) {
    EditCard(
        title = title,
    ) {
        Column {
            selectedItems.forEach { item ->
                ListItem(
                    headlineContent = { Text(text = itemName(item)) },
                    trailingContent = {
                        TooltipIconButton(
                            icon = Icons.Default.Delete,
                            tooltip = LocalStrings.current.delete,
                            onClick = {
                                selectedItems.remove(item)
                            },
                        )
                    },
                )
            }

            DropdownMenu(
                value = "",
                label = "",
            ) {
                val availableItems = remember(allItems, selectedItems) {
                    allItems.filter { !selectedItems.contains(it) }
                }

                availableItems.forEach { item ->
                    DropdownMenuItem(
                        text = itemName(item),
                        onClick = {
                            selectedItems.add(item)
                        },
                    )
                }
            }
        }
    }
}
