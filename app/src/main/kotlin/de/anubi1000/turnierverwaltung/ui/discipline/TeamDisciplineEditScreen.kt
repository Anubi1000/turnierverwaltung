package de.anubi1000.turnierverwaltung.ui.discipline

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditContent
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditScreenBase
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.SelectCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.TextField
import de.anubi1000.turnierverwaltung.viewmodel.discipline.TeamDisciplineEditViewModel

@Composable
fun TeamDisciplineEditScreen(
    navController: NavController,
    state: TeamDisciplineEditViewModel.State,
    onSaveButtonClick: () -> Unit,
    isEditMode: Boolean = false,
) {
    val strings = LocalStrings.current

    EditScreenBase(
        navController = navController,
        title = strings.editScreenTitle(isEditMode, strings.teamDiscipline),
        onSaveButtonClick = if (state is TeamDisciplineEditViewModel.State.Loaded && state.isValid.value) onSaveButtonClick else null,
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (state) {
            is TeamDisciplineEditViewModel.State.Loading -> LoadingIndicator(
                modifier = modifier,
            )
            is TeamDisciplineEditViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: TeamDisciplineEditViewModel.State.Loaded,
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
        }

        SelectCard(
            title = strings.basedOn,
            selectedItems = state.item.basedOn,
            allItems = state.disciplines,
            itemName = { it.name },
        )
    }
}
