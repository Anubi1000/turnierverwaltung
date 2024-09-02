package de.anubi1000.turnierverwaltung.ui.team.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.DropdownMenu
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
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: TeamEditViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    EditContent(
        modifier = modifier
    ) {
        EditCard(
            title = LocalStrings.current.general
        ) {
            TextField(
                value = state.team.name,
                onValueChange = { state.team.name = it },
                label = LocalStrings.current.name
            )

            TextField(
                value = state.team.startNumber.toString(),
                onValueChange = { newValue -> newValue.toIntOrNull()?.let { state.team.startNumber = it } },
                label = "Startnummer"
            )
        }

        EditCard(
            title = "Mitglieder"
        ) {
            Column {
                val listItemColors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
                state.team.members.forEach { memberId ->
                    ListItem(
                        headlineContent = {
                            Text(state.participants.find { it.id == memberId }!!.name)
                        },
                        trailingContent = {
                            TooltipIconButton(
                                icon = Icons.Default.Delete,
                                tooltip = LocalStrings.current.delete,
                                onClick = {
                                    state.team.members.remove(memberId)
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
                    state.participants.filter { !state.team.members.contains(it.id) }.forEach { participant ->
                        DropdownMenuItem(
                            text = { Text(participant.name) },
                            onClick = {
                                state.team.members.add(participant.id)
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