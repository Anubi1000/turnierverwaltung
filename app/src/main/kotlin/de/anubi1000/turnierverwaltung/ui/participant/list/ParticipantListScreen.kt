package de.anubi1000.turnierverwaltung.ui.participant.list

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.anubi1000.turnierverwaltung.data.participant.ListParticipant
import de.anubi1000.turnierverwaltung.navigation.old.ParticipantCreateDestination
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.viewmodel.ParticipantListViewModel
import org.mongodb.kbson.ObjectId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantListScreen(
    state: ParticipantListViewModel.State,
    tournamentId: ObjectId,
    deleteParticipant: (ListParticipant) -> Unit
) {
    val navigator = LocalNavigator.currentOrThrow

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LocalStrings.current.participants) },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.pop()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(LocalStrings.current.createParticipant) },
                icon = { Icon(Icons.Default.Add, null) },
                onClick = {
                    navigator.push(ParticipantCreateDestination(tournamentId))
                },
            )
        }
    ) {
        when (state) {
            is ParticipantListViewModel.State.Loading -> LoadingIndicator(
                modifier = Modifier.padding(it)
            )
            is ParticipantListViewModel.State.Loaded -> LoadedContent(
                state = state,
                deleteParticipant = deleteParticipant,
                modifier = Modifier.padding(it)
            )
        }
    }
}