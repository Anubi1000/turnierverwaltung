package de.anubi1000.turnierverwaltung.ui.tournament.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.DateField
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditContent
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditScreenBase
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.TextField
import de.anubi1000.turnierverwaltung.viewmodel.TournamentEditViewModel

@Composable
fun TournamentEditScreen(
    navController: NavController,
    state: TournamentEditViewModel.State,
    onSaveButtonClick: () -> Unit,
    isEditMode: Boolean = false
) {
    val strings = LocalStrings.current

    EditScreenBase(
        navController = navController,
        title = remember(isEditMode, state) {
            if (isEditMode) {
                var text = strings.editTournament
                if (state is TournamentEditViewModel.State.Loaded) text += ": ${state.originalName}"
                text
            } else {
                strings.createTournament
            }
        },
        onSaveButtonClick = if (state is TournamentEditViewModel.State.Loaded && state.isValid.value) onSaveButtonClick else null
    ) { padding ->
        when (state) {
            is TournamentEditViewModel.State.Loading -> LoadingIndicator()
            is TournamentEditViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun LoadedContent(
    state: TournamentEditViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    EditContent(
        modifier = modifier
    ) {
        EditCard(
            title = LocalStrings.current.general,
            width = 450.dp
        ) {
            TextField(
                label = LocalStrings.current.name,
                value = state.tournament.name,
                onValueChange = {
                    state.tournament.setName(it.replace("\n", ""))
                }
            )

            DateField(
                label = LocalStrings.current.dateOfTournament,
                date = state.tournament.date,
                onDateChange = {
                    state.tournament.setDate(it)
                }
            )
        }
    }
}