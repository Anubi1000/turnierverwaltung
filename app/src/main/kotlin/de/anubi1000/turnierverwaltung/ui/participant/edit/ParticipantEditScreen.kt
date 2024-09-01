package de.anubi1000.turnierverwaltung.ui.participant.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.DropdownMenu
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
    isEditMode: Boolean = false
) {
    val strings = LocalStrings.current

    EditScreenBase(
        navController = navController,
        title = strings.editScreenTitle(isEditMode, strings.participant),
        onSaveButtonClick = if (state is ParticipantEditViewModel.State.Loaded && state.isValid.value) onSaveButtonClick else null
    ) { padding ->
        when (state) {
            is ParticipantEditViewModel.State.Loading -> LoadingIndicator(
                modifier = Modifier.padding(padding)
            )
            is ParticipantEditViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadedContent(
    state: ParticipantEditViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    EditContent(
        modifier = modifier
    ) {
        val strings = LocalStrings.current

        EditCard(
            title = strings.general
        ) {
            TextField(
                value = state.participant.name,
                onValueChange = { state.participant.name = it.replace("\n", "") },
                label = strings.name
            )
            DropdownMenu(
                value = when (state.participant.gender) {
                    Participant.Gender.MALE -> "Männlich"
                    Participant.Gender.FEMALE -> "Weiblich"
                },
                label = "Geschlecht"
            ) {
                Participant.Gender.entries.forEach { entry ->
                    DropdownMenuItem(
                        text = {
                            Text(if (entry == Participant.Gender.MALE) "Männlich" else "Weiblich")
                        },
                        onClick = {
                            state.participant.gender = entry
                            it()
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
            DropdownMenu(
                value = remember(state.participant.clubId) {
                    val club = state.clubs.find { it.id == state.participant.clubId }
                    club?.name ?: ""
                },
                label = strings.club
            ) {
                state.clubs.forEach { club ->
                    DropdownMenuItem(
                        text = { Text(club.name) },
                        onClick = {
                            state.participant.clubId = club.id
                            it()
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}
