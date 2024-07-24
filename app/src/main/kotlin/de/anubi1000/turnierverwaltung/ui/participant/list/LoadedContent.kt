package de.anubi1000.turnierverwaltung.ui.participant.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.participant.ListParticipant
import de.anubi1000.turnierverwaltung.ui.participant.ParticipantDeleteDialog
import de.anubi1000.turnierverwaltung.viewmodel.ParticipantListViewModel

@Composable
fun LoadedContent(
    state: ParticipantListViewModel.State.Loaded,
    deleteParticipant: (ListParticipant) -> Unit,
    modifier: Modifier = Modifier
) {
    var participantToDelete: ListParticipant? by remember { mutableStateOf(null) }

    Box(
        modifier = modifier.padding(horizontal = 16.dp).fillMaxSize()
    ) {
        val items by state.participantFlow.collectAsState()
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(LocalStrings.current.noParticipantsExist)
            }
        } else {
            LazyColumn {
                itemsIndexed(items) { index, item ->
                    ParticipantListItem(
                        participant = item,
                        shouldColor = index % 2 == 0,
                        onDeleteButtonClick = {
                            participantToDelete = item
                        }
                    )
                }
                item(key = "fab_space") {
                    Spacer(Modifier.height(76.dp))
                }
            }
        }
    }

    if (participantToDelete != null) {
        ParticipantDeleteDialog(
            participantName = participantToDelete!!.name,
            onDismissRequest = { participantToDelete = null },
            onConfirmButtonClicked = {
                participantToDelete?.let(deleteParticipant)
                participantToDelete = null
            }
        )
    }
}