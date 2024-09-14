package de.anubi1000.turnierverwaltung.ui.team

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
                value = state.item.startNumber,
                onValueChange = { state.item.startNumber = it },
                label = strings.startNumber,
            )
        }

        SelectCard(
            title = strings.members,
            selectedItems = state.item.members,
            allItems = state.participants,
            itemName = { it.name },
        )

        SelectCard(
            title = strings.teamDisciplines,
            selectedItems = state.item.participatingDisciplines,
            allItems = state.teamDisciplines,
            itemName = { it.name },
        )
    }
}
