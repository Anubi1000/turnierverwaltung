package de.anubi1000.turnierverwaltung.ui.discipline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.edit.EditDiscipline
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCardScope
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditContent
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditScreenBase
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.ListItem
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.TextField
import de.anubi1000.turnierverwaltung.viewmodel.discipline.DisciplineEditViewModel

@Composable
fun DisciplineEditScreen(
    navController: NavController,
    state: DisciplineEditViewModel.State,
    onSaveButtonClick: () -> Unit,
    isEditMode: Boolean = false,
) {
    val strings = LocalStrings.current

    EditScreenBase(
        navController = navController,
        title = strings.editScreenTitle(isEditMode, strings.discipline),
        onSaveButtonClick = if (state is DisciplineEditViewModel.State.Loaded && state.isValid.value) onSaveButtonClick else null,
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (state) {
            is DisciplineEditViewModel.State.Loading -> LoadingIndicator(
                modifier = modifier,
            )
            is DisciplineEditViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: DisciplineEditViewModel.State.Loaded,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current

    EditContent(
        modifier = modifier,
    ) {
        EditCard(
            title = strings.general,
        ) {
            TextField(
                label = strings.name,
                value = state.item.name,
                onValueChange = { state.item.name = it },
            )

            ListItem(
                headlineContent = { Text(strings.genderSeparated) },
                trailingContent = {
                    Checkbox(
                        checked = state.item.isGenderSeparated,
                        onCheckedChange = { state.item.isGenderSeparated = it },
                    )
                },
            )

            TextField(
                label = strings.amountOfResultsOnDisplay,
                value = state.item.amountOfBestRoundsToShow,
                onValueChange = { state.item.amountOfBestRoundsToShow = it },
            )
        }

        EditCard(
            title = strings.values,
        ) {
            Column {
                state.item.values.forEach { value ->
                    ValueListItem(
                        value = value,
                        state = state,
                    )
                }

                Button(
                    onClick = {
                        state.item.values.add(EditDiscipline.Value())
                    },
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 4.dp).fillMaxWidth(),
                ) {
                    Text(strings.newValue)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCardScope.ValueListItem(
    value: EditDiscipline.Value,
    state: DisciplineEditViewModel.State.Loaded,
) {
    val strings = LocalStrings.current

    ListItem(
        headlineContent = {
            OutlinedTextField(
                value = value.name,
                onValueChange = {
                    value.name = it.replace("\n", "")
                },
                singleLine = true,
                label = { Text(strings.name) },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        trailingContent = {
            Row {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip { Text(text = strings.isAdded) }
                    },
                    state = rememberTooltipState(),
                    content = {
                        Checkbox(
                            checked = value.isAdded,
                            onCheckedChange = {
                                value.isAdded = it
                            },
                        )
                    },
                )

                TooltipIconButton(
                    icon = Icons.Default.Delete,
                    tooltip = strings.delete,
                    onClick = {
                        state.item.values.remove(value)
                    },
                )
            }
        },
    )
}
