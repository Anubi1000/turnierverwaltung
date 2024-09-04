package de.anubi1000.turnierverwaltung.ui.club

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditContent
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditScreenBase
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.TextField
import de.anubi1000.turnierverwaltung.viewmodel.club.ClubEditViewModel

@Composable
fun ClubEditScreen(
    navController: NavController,
    state: ClubEditViewModel.State,
    onSaveButtonClick: () -> Unit,
    isEditMode: Boolean = false
) {
    val strings = LocalStrings.current

    EditScreenBase(
        navController = navController,
        title = strings.editScreenTitle(isEditMode, LocalStrings.current.club),
        onSaveButtonClick = if (state is ClubEditViewModel.State.Loaded && state.isValid.value) onSaveButtonClick else null
    ) { padding ->
        when (state) {
            is ClubEditViewModel.State.Loading -> LoadingIndicator()
            is ClubEditViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: ClubEditViewModel.State.Loaded,
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
                onValueChange = {
                    state.item.name = it
                }
            )
        }
    }
}
