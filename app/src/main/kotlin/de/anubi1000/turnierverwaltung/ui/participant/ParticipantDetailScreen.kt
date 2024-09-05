package de.anubi1000.turnierverwaltung.ui.participant

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantEditDestination
import de.anubi1000.turnierverwaltung.ui.util.DeleteDialog
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailCard
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailContent
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailItem
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailScreenBase
import de.anubi1000.turnierverwaltung.viewmodel.participant.ParticipantDetailViewModel

@Composable
fun ParticipantDetailScreen(
    navController: NavController,
    state: ParticipantDetailViewModel.State,
    onDeleteButtonClick: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DetailScreenBase(
        navController = navController,
        title = LocalStrings.current.participant,
        onEditButtonClick = {
            if (state is ParticipantDetailViewModel.State.Loaded) {
                navController.navigate(ParticipantEditDestination(state.item.id))
            }
        },
        onDeleteButtonClick = {
            showDeleteDialog = true
        }
    ) { padding ->
        when (state) {
            is ParticipantDetailViewModel.State.Loading -> LoadingIndicator()
            is ParticipantDetailViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (showDeleteDialog && state is ParticipantDetailViewModel.State.Loaded) {
        DeleteDialog(
            itemName = state.item.name,
            onDismissRequest = { showDeleteDialog = false },
            onConfirmButtonClick = {
                showDeleteDialog = false
                onDeleteButtonClick()
            }
        )
    }
}

@Composable
private fun LoadedContent(
    state: ParticipantDetailViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    DetailContent(
        modifier = modifier
    ) {
        val strings = LocalStrings.current

        DetailCard(
            title = strings.general
        ) {
            DetailItem(
                headlineText = state.item.name,
                overlineText = strings.name
            )

            DetailItem(
                headlineText = state.item.startNumber.toString(),
                overlineText = strings.startNumber
            )

            DetailItem(
                headlineText = strings.genderName(state.item.gender),
                overlineText = strings.gender
            )

            DetailItem(
                headlineText = state.item.club!!.name,
                overlineText = strings.club
            )
        }
    }
}