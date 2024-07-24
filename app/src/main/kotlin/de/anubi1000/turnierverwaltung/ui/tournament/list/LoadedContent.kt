package de.anubi1000.turnierverwaltung.ui.tournament.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentEditDestination
import de.anubi1000.turnierverwaltung.viewmodel.TournamentListViewModel

@Composable
fun LoadedContent(
    state: TournamentListViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.currentOrThrow

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        val items by state.tournamentFlow.collectAsState()
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(LocalStrings.current.noTournamentsExist)
            }
        } else {
            val screenId by remember(navigator) {
                derivedStateOf {
                    when (val screen = navigator.lastItem) {
                        is TournamentDetailDestination -> screen.tournamentId
                        is TournamentEditDestination -> screen.tournamentId
                        else -> null
                    }
                }
            }
            LazyColumn {
                items(items) { item ->
                    TournamentListItem(
                        tournament = item,
                        selected = screenId == item.id,
                        onClick = {
                            navigator.popUntilRoot()
                            navigator.push(TournamentDetailDestination(item.id))
                        }
                    )
                }
                item(key = "fab_space") {
                    Spacer(Modifier.height(76.dp))
                }
            }
        }
    }
}