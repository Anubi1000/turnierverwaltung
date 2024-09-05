package de.anubi1000.turnierverwaltung.ui.discipline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.discipline.DisciplineEditDestination
import de.anubi1000.turnierverwaltung.ui.util.DeleteDialog
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailCard
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailContent
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailItem
import de.anubi1000.turnierverwaltung.ui.util.screen.detail.DetailScreenBase
import de.anubi1000.turnierverwaltung.viewmodel.discipline.DisciplineDetailViewModel

@Composable
fun DisciplineDetailScreen(
    navController: NavController,
    state: DisciplineDetailViewModel.State,
    onDeleteButtonClick: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DetailScreenBase(
        navController = navController,
        title = LocalStrings.current.discipline,
        onEditButtonClick = {
            if (state is DisciplineDetailViewModel.State.Loaded) {
                 navController.navigate(DisciplineEditDestination(state.item.id))
            }
        },
        onDeleteButtonClick = {
            showDeleteDialog = true
        }
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (state) {
            is DisciplineDetailViewModel.State.Loading -> LoadingIndicator(
                modifier = modifier
            )
            is DisciplineDetailViewModel.State.Loaded -> LoadedContent(
                state = state,
                modifier = modifier
            )
        }
    }

    if (showDeleteDialog && state is DisciplineDetailViewModel.State.Loaded) {
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
    state: DisciplineDetailViewModel.State.Loaded,
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
        }

        DetailCard(
            title = strings.values
        ) {
            Column {
                state.item.values.forEach { value ->
                    DetailItem(
                        headlineText = value.name,
                        supportingText = strings.isAddedYesNo(value.isAdded)
                    )
                }
            }
        }
    }
}