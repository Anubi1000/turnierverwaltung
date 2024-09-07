package de.anubi1000.turnierverwaltung.ui.discipline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
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
import de.anubi1000.turnierverwaltung.data.EditDiscipline
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.DropdownMenu
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditContent
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditScreenBase
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.TextField
import de.anubi1000.turnierverwaltung.viewmodel.discipline.DisciplineEditViewModel
import de.anubi1000.turnierverwaltung.viewmodel.discipline.TeamDisciplineEditViewModel

@Composable
fun TeamDisciplineEditScreen(
    navController: NavController,
    state: TeamDisciplineEditViewModel.State,
    onSaveButtonClick: () -> Unit,
    isEditMode: Boolean = false
) {
    val strings = LocalStrings.current

    EditScreenBase(
        navController = navController,
        title = strings.editScreenTitle(isEditMode, strings.teamDiscipline),
        onSaveButtonClick = if (state is TeamDisciplineEditViewModel.State.Loaded && state.isValid.value) onSaveButtonClick else null
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (state) {
            is TeamDisciplineEditViewModel.State.Loading -> LoadingIndicator(
                modifier = modifier
            )
            is TeamDisciplineEditViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: TeamDisciplineEditViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current

    EditContent(
        modifier = modifier
    ) {
        EditCard(
            title = strings.general
        ) {
            TextField(
                label = strings.name,
                value = state.item.name,
                onValueChange = { state.item.name = it }
            )
        }

        EditCard(
            title = strings.basedOn
        ) {
            Column {
                val listItemColors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
                state.item.basedOn.forEach { disciplineId ->
                    ListItem(
                        headlineContent = {
                            Text(state.disciplines.find { it.id == disciplineId }!!.name)
                        },
                        trailingContent = {
                            TooltipIconButton(
                                icon = Icons.Default.Delete,
                                tooltip = strings.delete,
                                onClick = {
                                    state.item.basedOn.remove(disciplineId)
                                }
                            )
                        },
                        colors = listItemColors
                    )
                }

                DropdownMenu(
                    value = "",
                    label = "",
                ) {
                    state.disciplines.filter { !state.item.basedOn.contains(it.id) }.forEach { discipline ->
                        DropdownMenuItem(
                            text = { Text(discipline.name) },
                            onClick = {
                                state.item.basedOn.add(discipline.id)
                                it()
                            },
                            contentPadding = MenuDefaults.DropdownMenuItemContentPadding
                        )
                    }
                }
            }
        }
    }
}
