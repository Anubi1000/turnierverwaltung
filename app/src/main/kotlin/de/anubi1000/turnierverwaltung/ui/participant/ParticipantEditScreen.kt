package de.anubi1000.turnierverwaltung.ui.participant

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.DropdownMenu
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.DropdownMenuItem
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditContent
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditScreenBase
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.TextField
import de.anubi1000.turnierverwaltung.viewmodel.participant.ParticipantEditViewModel

@Composable
fun ParticipantEditScreen(
    navController: NavController,
    state: ParticipantEditViewModel.State,
    onSaveButtonClick: () -> Unit = {},
    isEditMode: Boolean = false,
) {
    val strings = LocalStrings.current

    EditScreenBase(
        navController = navController,
        title = strings.editScreenTitle(isEditMode, strings.participant),
        onSaveButtonClick = if (state is ParticipantEditViewModel.State.Loaded && state.isValid.value) {
            onSaveButtonClick
        } else {
            null
        },
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (state) {
            is ParticipantEditViewModel.State.Loading -> LoadingIndicator(
                modifier = modifier,
            )
            is ParticipantEditViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: ParticipantEditViewModel.State.Loaded,
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
                value = state.item.startNumber,
                onValueChange = { state.item.startNumber = it },
                label = strings.startNumber,
            )

            DropdownMenu(
                value = strings.genderName(state.item.gender),
                label = strings.gender,
            ) {
                Participant.Gender.entries.forEach { entry ->
                    DropdownMenuItem(
                        text = strings.genderName(entry),
                        selected = state.item.gender == entry,
                        onClick = {
                            state.item.gender = entry
                        },
                    )
                }
            }

            DropdownMenu(
                value = state.item.club?.name ?: "",
                label = strings.club,
            ) {
                state.clubs.forEach { club ->
                    DropdownMenuItem(
                        text = club.name,
                        selected = state.item.club == club,
                        onClick = {
                            state.item.club = club
                        },
                    )
                }
            }
        }
    }
}
