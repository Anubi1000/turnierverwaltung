package de.anubi1000.turnierverwaltung.ui.participant

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.EditParticipantResult
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditCard
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditContent
import de.anubi1000.turnierverwaltung.ui.util.screen.edit.EditScreenBase
import de.anubi1000.turnierverwaltung.viewmodel.participant.ParticipantResultViewModel

@Composable
fun ParticipantResultScreen(
    navController: NavController,
    state: ParticipantResultViewModel.State,
    onSaveButtonClick: () -> Unit = {},
) {
    EditScreenBase(
        navController = navController,
        title = LocalStrings.current.inputPoints,
        onSaveButtonClick = if (state is ParticipantResultViewModel.State.Loaded && state.isValid.value) onSaveButtonClick else null,
    ) {
        val modifier = Modifier.padding(it)
        when (state) {
            ParticipantResultViewModel.State.Loading -> LoadingIndicator(
                modifier = modifier,
            )
            is ParticipantResultViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun LoadedContent(state: ParticipantResultViewModel.State.Loaded, modifier: Modifier = Modifier) {
    EditContent(modifier = modifier) {
        EditCard(
            title = LocalStrings.current.points,
            width = 700.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
            ) {
                state.result.rounds.forEach { round ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        state.discipline.values.forEach { value ->
                            OutlinedTextField(
                                value = round.values[value.id].toString(),
                                onValueChange = { round.values[value.id] = it },
                                label = {
                                    Text(value.name)
                                },
                                singleLine = true,
                                modifier = Modifier.weight(1f, true).padding(horizontal = 2.dp),
                            )
                        }
                        TooltipIconButton(
                            icon = Icons.Default.Delete,
                            tooltip = LocalStrings.current.delete,
                            onClick = {
                                state.result.rounds.remove(round)
                            },
                        )
                    }
                }
                Button(
                    onClick = {
                        state.result.rounds.add(
                            EditParticipantResult.RoundResult(
                                values = state.discipline.values.map {
                                    it.id to ""
                                }.toMutableStateMap(),
                            ),
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                ) {
                    Text(text = LocalStrings.current.newRound)
                }
            }
        }
    }
}
