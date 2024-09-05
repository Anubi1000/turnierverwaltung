package de.anubi1000.turnierverwaltung.ui.discipline

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
import de.anubi1000.turnierverwaltung.navigation.discipline.DisciplineDetailDestination
import de.anubi1000.turnierverwaltung.navigation.discipline.DisciplineEditDestination
import de.anubi1000.turnierverwaltung.navigation.discipline.DisciplineListDestination
import de.anubi1000.turnierverwaltung.ui.util.CenteredText
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.SelectableListItem
import de.anubi1000.turnierverwaltung.ui.util.screen.list.ListBase
import de.anubi1000.turnierverwaltung.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.util.getCurrentDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.discipline.DisciplineListViewModel

@Composable
fun DisciplineList(
    navController: NavController,
    state: DisciplineListViewModel.State,
    modifier: Modifier = Modifier
) {
    ListBase(
        title = LocalStrings.current.disciplines,
        onCreateButtonClick = {
            val currentDestination = navController.getCurrentDestination()
            if (currentDestination !is DisciplineEditDestination || currentDestination.id != null) {
                navController.navigate(DisciplineEditDestination())
            }
        },
        modifier = modifier
    ) {
        when (state) {
            is DisciplineListViewModel.State.Loading -> LoadingIndicator()
            is DisciplineListViewModel.State.Loaded -> LoadedContent(
                navController = navController,
                state = state
            )
        }
    }
}

@Composable
private fun LoadedContent(
    navController: NavController,
    state: DisciplineListViewModel.State.Loaded,
    modifier: Modifier = Modifier
) {
    val items by state.itemFlow.collectAsStateWithLifecycle()

    if (items.isEmpty()) {
        val strings = LocalStrings.current

        CenteredText(
            text = strings.xDontExist(strings.disciplines)
        )
    } else {
        val currentDestination by navController.currentDestinationAsState()

        val currentItemId by remember(navController) {
            derivedStateOf {
                when (val destination = currentDestination) {
                    is DisciplineDetailDestination -> destination.id
                    is DisciplineEditDestination -> destination.id
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
                            navController.navigate(DisciplineDetailDestination(item.id)) {
                                popUpTo<DisciplineListDestination>()
                            }
                        }
                    },
                    modifier = itemModifier
                )
            }
        }
    }
}