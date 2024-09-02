package de.anubi1000.turnierverwaltung.ui.participant.detail

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantEditDestination
import de.anubi1000.turnierverwaltung.ui.util.DeleteDialog
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailCard
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailContent
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailItem
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailScreenBase
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseDetailViewModel

@Composable
fun ParticipantDetailScreen(
    navController: NavController,
    state: BaseDetailViewModel.State,
    onDeleteButtonClick: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DetailScreenBase(
        navController = navController,
        title = LocalStrings.current.participant,
        onEditButtonClick = {
            if (state is BaseDetailViewModel.State.Loaded<*>) {
                @Suppress("UNCHECKED_CAST")
                state as BaseDetailViewModel.State.Loaded<Participant>

                navController.navigate(ParticipantEditDestination(state.item.id))
            }
        },
        onDeleteButtonClick = {
            showDeleteDialog = true
        }
    ) { padding ->
        @Suppress("UNCHECKED_CAST")
        when (state) {
            is BaseDetailViewModel.State.Loading -> LoadingIndicator()
            is BaseDetailViewModel.State.Loaded<*> -> LoadedContent(
                state = state as BaseDetailViewModel.State.Loaded<Participant>,
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (showDeleteDialog && state is BaseDetailViewModel.State.Loaded<*>) {
        @Suppress("UNCHECKED_CAST")
        state as BaseDetailViewModel.State.Loaded<Participant>

        DeleteDialog(
            itemName = state.item.name,
            onDismissRequest = { showDeleteDialog = false },
            onConfirmButtonClick = {
                showDeleteDialog = false
                navController.popBackStack()
                onDeleteButtonClick()
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LoadedContent(
    state: BaseDetailViewModel.State.Loaded<Participant>,
    modifier: Modifier = Modifier
) {
    DetailContent(
        modifier = modifier
    ) {
        val strings = LocalStrings.current

        DetailCard(
            title = strings.general,
            modifier = Modifier.width(450.dp).fillMaxRowHeight()
        ) {
            DetailItem(
                headlineText = state.item.name,
                overlineText = strings.name
            )

            DetailItem(
                headlineText = state.item.startNumber.toString(),
                overlineText = "Startnummer"
            )

            DetailItem(
                headlineText = if (state.item.gender == Participant.Gender.MALE) "Männlich" else "Weiblich",
                overlineText = "Geschlecht"
            )

            DetailItem(
                headlineText = state.item.club!!.name,
                overlineText = strings.clubs
            )
        }
    }
}