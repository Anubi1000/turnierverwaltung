package de.anubi1000.turnierverwaltung.ui.tournament.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.viewmodel.TournamentEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentEditScreen(
    state: TournamentEditViewModel.State,
    onSaveButtonClick: () -> Unit,
    isEditMode: Boolean = false
) {
    val navigator = LocalNavigator.currentOrThrow

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditMode) LocalStrings.current.editTournament else LocalStrings.current.createTournament)
                },
                navigationIcon = {
                    TooltipIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        tooltip = LocalStrings.current.back,
                        onClick = {
                            navigator.pop()
                        }
                    )
                },
                actions = {
                    val isEnabled by remember(state) {
                        derivedStateOf {
                            state is TournamentEditViewModel.State.Loaded &&
                                    state.isValid.value
                        }
                    }

                    TooltipIconButton(
                        icon = Icons.Default.Save,
                        tooltip = LocalStrings.current.save,
                        onClick = onSaveButtonClick,
                        enabled = isEnabled
                    )
                }
            )
        }
    ) {
        Card(
            modifier = Modifier.padding(it).padding(bottom = 16.dp, end = 16.dp).fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            when (state) {
                is TournamentEditViewModel.State.Loading -> LoadingIndicator()
                is TournamentEditViewModel.State.Loaded -> LoadedContent(
                    state = state,
                    isEditMode = isEditMode
                )
            }
        }
    }
}
