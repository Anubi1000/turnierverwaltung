package de.anubi1000.turnierverwaltung.ui.club.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.EditClub
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditContent
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditScreenBase
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.TextField
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseEditViewModel

@Composable
fun ClubEditScreen(
    navController: NavController,
    state: BaseEditViewModel.State,
    onSaveButtonClick: () -> Unit,
    isEditMode: Boolean = false
) {
    val strings = LocalStrings.current

    EditScreenBase(
        navController = navController,
        title = strings.editScreenTitle(isEditMode, strings.club),
        onSaveButtonClick = if (state is BaseEditViewModel.State.Loaded<*> && state.isValid.value) onSaveButtonClick else null
    ) { padding ->
        @Suppress("UNCHECKED_CAST")
        when (state) {
            is BaseEditViewModel.State.Loading -> LoadingIndicator()
            is BaseEditViewModel.State.Loaded<*> -> LoadedContent(
                state = state as BaseEditViewModel.State.Loaded<EditClub>,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: BaseEditViewModel.State.Loaded<EditClub>,
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
        }
    }
}
