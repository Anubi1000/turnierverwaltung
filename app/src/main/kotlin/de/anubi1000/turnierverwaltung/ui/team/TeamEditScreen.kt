package de.anubi1000.turnierverwaltung.ui.team

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.DropdownMenu
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.DropdownMenuItem
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditContent
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditScreenBase
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.TextField
import de.anubi1000.turnierverwaltung.viewmodel.team.TeamEditViewModel

@Composable
fun TeamEditScreen(
    navController: NavController,
    state: TeamEditViewModel.State,
    onSaveButtonClick: () -> Unit,
    isEditMode: Boolean = false,
) {
    val strings = LocalStrings.current

    EditScreenBase(
        navController = navController,
        title = strings.editScreenTitle(isEditMode, strings.team),
        onSaveButtonClick = if (state is TeamEditViewModel.State.Loaded && state.isValid.value) onSaveButtonClick else null,
    ) { padding ->
        when (state) {
            is TeamEditViewModel.State.Loading -> LoadingIndicator()
            is TeamEditViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: TeamEditViewModel.State.Loaded,
    modifier: Modifier = Modifier,
) {
    EditContent(
        modifier = modifier,
    ) {
        val strings = LocalStrings.current
        EditCard(
            title = strings.general,
        ) {
            TextField(
                value = state.item.name,
                onValueChange = { state.item.name = it },
                label = strings.name,
            )

            TextField(
                value = state.item.startNumber.toString(),
                onValueChange = { newValue -> newValue.toIntOrNull()?.let { state.item.startNumber = it } },
                label = strings.startNumber,
            )
        }

        EditCard(
            title = strings.members,
        ) {
            Column {
                val listItemColors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                )
                state.item.members.forEach { memberId ->
                    ListItem(
                        headlineContent = {
                            Text(state.participants.find { it.id == memberId }!!.name)
                        },
                        trailingContent = {
                            TooltipIconButton(
                                icon = Icons.Default.Delete,
                                tooltip = strings.delete,
                                onClick = {
                                    state.item.members.remove(memberId)
                                },
                            )
                        },
                        colors = listItemColors,
                    )
                }

                DropdownMenu(
                    value = "",
                    label = "",
                ) {
                    val availableParticipants = remember(state.participants, state.item.members) {
                        state.participants.filter { !state.item.members.contains(it.id) }
                    }

                    availableParticipants.forEach { participant ->
                        DropdownMenuItem(
                            text = participant.name,
                            onClick = {
                                state.item.members.add(participant.id)
                            },
                        )
                    }
                }
            }
        }

        EditCard(
            title = strings.teamDisciplines,
        ) {
            Column {
                val listItemColors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                )
                state.item.participatingDisciplines.forEach { disciplineId ->
                    ListItem(
                        headlineContent = {
                            Text(state.teamDisciplines.find { it.id == disciplineId }!!.name)
                        },
                        trailingContent = {
                            TooltipIconButton(
                                icon = Icons.Default.Delete,
                                tooltip = strings.delete,
                                onClick = {
                                    state.item.participatingDisciplines.remove(disciplineId)
                                },
                            )
                        },
                        colors = listItemColors,
                    )
                }

                DropdownMenu(
                    value = "",
                    label = "",
                ) {
                    val availableDisciplines = remember(state.teamDisciplines, state.item.participatingDisciplines) {
                        state.teamDisciplines.filter { !state.item.participatingDisciplines.contains(it.id) }
                    }

                    availableDisciplines.forEach { discipline ->
                        DropdownMenuItem(
                            text = discipline.name,
                            onClick = {
                                state.item.participatingDisciplines.add(discipline.id)
                            },
                        )
                    }
                }
            }
        }
    }
}
