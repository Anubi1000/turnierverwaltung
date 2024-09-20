package de.anubi1000.turnierverwaltung.ui.club

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.club.ClubEditDestination
import de.anubi1000.turnierverwaltung.ui.util.DeleteDialog
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailCard
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailContent
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailItem
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailScreenBase
import de.anubi1000.turnierverwaltung.viewmodel.club.ClubDetailViewModel

@Composable
fun ClubDetailScreen(
    navController: NavController,
    state: ClubDetailViewModel.State,
    onDeleteButtonClick: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DetailScreenBase(
        navController = navController,
        title = LocalStrings.current.club,
        onEditButtonClick = {
            if (state is ClubDetailViewModel.State.Loaded) {
                navController.navigate(ClubEditDestination(state.item.id))
            }
        },
        onDeleteButtonClick = if (state is ClubDetailViewModel.State.Loaded && state.participantsWithClub == 0) {
            {
                showDeleteDialog = true
            } 
        } else {
            null
        },
    ) { padding ->
        when (state) {
            is ClubDetailViewModel.State.Loading -> LoadingIndicator()
            is ClubDetailViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = Modifier.padding(padding),
            )
        }
    }

    if (showDeleteDialog && state is ClubDetailViewModel.State.Loaded) {
        DeleteDialog(
            itemName = state.item.name,
            onDismissRequest = { showDeleteDialog = false },
            onConfirmButtonClick = {
                showDeleteDialog = false
                onDeleteButtonClick()
            },
        )
    }
}

@Composable
private fun LoadedContent(
    state: ClubDetailViewModel.State.Loaded,
    modifier: Modifier = Modifier,
) {
    DetailContent(
        modifier = modifier,
    ) {
        val strings = LocalStrings.current

        DetailCard(
            title = strings.general,
        ) {
            DetailItem(
                headlineText = state.item.name,
                overlineText = strings.name,
            )
            DetailItem(
                headlineText = state.participantsWithClub.toString(),
                overlineText = strings.participantsWithClub,
            )
        }
    }
}
