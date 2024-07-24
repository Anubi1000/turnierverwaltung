package de.anubi1000.turnierverwaltung.ui.tournament.list

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentCreateDestination
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.viewmodel.TournamentListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentListScreen(
    state: TournamentListViewModel.State
) {
    val navigator = LocalNavigator.currentOrThrow

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LocalStrings.current.tournaments) },
                actions = {
                    TooltipIconButton(
                        icon = Icons.Default.Add,
                        tooltip = LocalStrings.current.createTournament,
                        onClick = {
                            if (navigator.lastItem !is TournamentCreateDestination) {
                                navigator.push(TournamentCreateDestination())
                            }
                        }
                    )
                }
            )
        },
        modifier = Modifier.width(420.dp)
    ) {
        when (state) {
            is TournamentListViewModel.State.Loading -> LoadingIndicator(
                modifier = Modifier.padding(it)
            )
            is TournamentListViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = Modifier.padding(it)
            )
        }
    }
}