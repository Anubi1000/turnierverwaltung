package de.anubi1000.turnierverwaltung.ui.tournament

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentEditViewModel

@Composable
fun TournamentEditScreen(
    navController: NavController,
    state: TournamentEditViewModel.State,
    onSaveButtonClick: () -> Unit,
    isEditMode: Boolean,
) {
    val strings = LocalStrings.current

    EditScreenBase(
        navController = navController,
        title = strings.editScreenTitle(isEditMode, strings.tournament),
        onSaveButtonClick = if (state is TournamentEditViewModel.State.Loaded && state.isValid.value) onSaveButtonClick else null,
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (state) {
            is TournamentEditViewModel.State.Loading -> LoadingIndicator(
                modifier = modifier,
            )
            is TournamentEditViewModel.State.Loaded -> LoadedContent(
                state = state,
                isEditMode = isEditMode,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: TournamentEditViewModel.State.Loaded,
    isEditMode: Boolean,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current

    EditContent(
        modifier = modifier,
    ) {
        EditCard(
            title = strings.general,
            width = 450.dp,
        ) {
            TextField(
                label = strings.name,
                value = state.item.name,
                onValueChange = { state.item.name = it },
            )

            DateField(
                label = strings.dateOfTournament,
                date = state.item.date,
                onDateChange = { state.item.date = it },
            )

            TextField(
                label = strings.teamSize,
                value = state.item.teamSize,
                onValueChange = { state.item.teamSize = it },
                readOnly = isEditMode,
            )
        }
    }
}
