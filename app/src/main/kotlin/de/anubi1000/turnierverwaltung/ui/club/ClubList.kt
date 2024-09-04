package de.anubi1000.turnierverwaltung.ui.club

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.club.ClubDetailDestination
import de.anubi1000.turnierverwaltung.navigation.club.ClubEditDestination
import de.anubi1000.turnierverwaltung.navigation.club.ClubListDestination
import de.anubi1000.turnierverwaltung.ui.util.CenteredText
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.SelectableListItem
import de.anubi1000.turnierverwaltung.ui.util.screen.list.ListBase
import de.anubi1000.turnierverwaltung.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.util.getCurrentDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.club.ClubListViewModel

@Composable
fun ClubList(
    navController: NavController,
    state: ClubListViewModel.State,
    modifier: Modifier = Modifier
) {
    ListBase(
        title = LocalStrings.current.clubs,
        onCreateButtonClick = {
            val currentDestination = navController.getCurrentDestination()
            if (currentDestination !is ClubEditDestination || currentDestination.id != null) {
                navController.navigate(ClubEditDestination())
            }
        },
        modifier = modifier
    ) {
        when (state) {
            is ClubListViewModel.State.Loading -> LoadingIndicator()
            is ClubListViewModel.State.Loaded -> LoadedContent(
                navController = navController,
                state = state
            )
        }
    }
}

@Composable
private fun LoadedContent(
    navController: NavController,
    state: ClubListViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    val items by state.clubFlow.collectAsStateWithLifecycle()

    if (items.isEmpty()) {
        CenteredText(
            text = LocalStrings.current.xDontExist(LocalStrings.current.clubs)
        )
    } else {
        val currentDestination by navController.currentDestinationAsState()

        val currentItemId by remember(navController) {
            derivedStateOf {
                when (val destination = currentDestination) {
                    is ClubDetailDestination -> destination.id
                    is ClubEditDestination -> destination.id
                    else -> null
                }?.toObjectId()
            }
        }

        val itemModifier = Modifier.padding(2.dp)
        LazyColumn(
            modifier = modifier
        ) {
            items(items, key = { it.id }) { item ->
                SelectableListItem(
                    headlineContent = { Text(item.name) },
                    selected = currentItemId == item.id,
                    onClick = {
                        if (currentItemId != item.id) {
                            navController.navigate(ClubDetailDestination(item.id)) {
                                popUpTo<ClubListDestination>()
                            }
                        }
                    },
                    modifier = itemModifier
                )
            }
        }
    }
}