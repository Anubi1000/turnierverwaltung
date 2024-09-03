package de.anubi1000.turnierverwaltung.ui.discipline.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.EditDiscipline
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditContent
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditScreenBase
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.TextField
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseEditViewModel

@Composable
fun DisciplineEditScreen(
    navController: NavController,
    state: BaseEditViewModel.State,
    onSaveButtonClick: () -> Unit,
    isEditMode: Boolean = false
) {
    val strings = LocalStrings.current

    EditScreenBase(
        navController = navController,
        title = strings.editScreenTitle(isEditMode, "Disziplin"),
        onSaveButtonClick = if (state is BaseEditViewModel.State.Loaded<*> && state.isValid.value) onSaveButtonClick else null
    ) { padding ->
        @Suppress("UNCHECKED_CAST")
        when (state) {
            is BaseEditViewModel.State.Loading -> LoadingIndicator()
            is BaseEditViewModel.State.Loaded<*> -> LoadedContent(
                state = state as BaseEditViewModel.State.Loaded<EditDiscipline>,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: BaseEditViewModel.State.Loaded<EditDiscipline>,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current

    EditContent(
        modifier = modifier
    ) {
        EditCard(
            title = strings.general,
            width = 450.dp
        ) {
            TextField(
                label = strings.name,
                value = state.item.name,
                onValueChange = {
                    state.item.name = it
                }
            )
            ListItem(
                headlineContent = { Text("Geschlechter getrennt") },
                trailingContent = {
                    Checkbox(
                        checked = state.item.isGenderSeparated,
                        onCheckedChange = { state.item.isGenderSeparated = it }
                    )
                }
            )
        }

        EditCard(
            title = "Werte",
            width = 450.dp
        ) {
            Column {
                state.item.values.forEach { value ->
                    ListItem(
                        headlineContent = {
                            OutlinedTextField(
                                value = value.name,
                                onValueChange = {
                                    value.name = it
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        trailingContent = {
                            Row {
                                Checkbox(
                                    checked = value.isAdded,
                                    onCheckedChange = {
                                        value.isAdded = it
                                    }
                                )
                                TooltipIconButton(
                                    icon = Icons.Default.Delete,
                                    tooltip = LocalStrings.current.delete,
                                    onClick = {
                                        state.item.values.remove(value)
                                    }
                                )
                            }
                        }
                    )
                }

                Button(
                    onClick = {
                        state.item.values.add(EditDiscipline.Value())
                    },
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp).fillMaxWidth(),
                ) {
                    Text("Neuer Wert")
                }
            }
        }
    }
}
