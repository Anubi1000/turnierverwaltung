package de.anubi1000.turnierverwaltung.ui.tournament.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.EditTournament
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.DateField
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditContent
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditScreenBase
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.TextField
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseEditViewModel
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentEditViewModel

@Composable
fun TournamentEditScreen(
    navController: NavController,
    state: BaseEditViewModel.State,
    onSaveButtonClick: () -> Unit,
    isEditMode: Boolean = false
) {
    EditScreenBase(
        navController = navController,
        title = if (isEditMode) LocalStrings.current.editTournament else LocalStrings.current.createTournament,
        onSaveButtonClick = if (state is BaseEditViewModel.State.Loaded<*> && state.isValid.value) onSaveButtonClick else null
    ) { padding ->
        @Suppress("UNCHECKED_CAST")
        when (state) {
            is BaseEditViewModel.State.Loading -> LoadingIndicator()
            is BaseEditViewModel.State.Loaded<*> -> LoadedContent(
                state = state as BaseEditViewModel.State.Loaded<EditTournament>,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: BaseEditViewModel.State.Loaded<EditTournament>,
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
                    state.item.setName(it.replace("\n", ""))
                }
            )

            DateField(
                label = strings.dateOfTournament,
                date = state.item.date,
                onDateChange = {
                    state.item.setDate(it)
                }
            )
        }
    }
}