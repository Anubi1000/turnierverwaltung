package de.anubi1000.turnierverwaltung.ui.discipline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.discipline.DisciplineDetailDestination
import de.anubi1000.turnierverwaltung.navigation.discipline.DisciplineEditDestination
import de.anubi1000.turnierverwaltung.navigation.discipline.DisciplineListDestination
import de.anubi1000.turnierverwaltung.navigation.discipline.TeamDisciplineDetailDestination
import de.anubi1000.turnierverwaltung.navigation.discipline.TeamDisciplineEditDestination
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.screen.list.ListBase
import de.anubi1000.turnierverwaltung.ui.util.screen.list.SelectableListItem
import de.anubi1000.turnierverwaltung.util.currentDestinationAsState
import de.anubi1000.turnierverwaltung.util.getCurrentDestination
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.discipline.DisciplineListViewModel

@Composable
fun DisciplineList(
    navController: NavController,
    state: DisciplineListViewModel.State,
    modifier: Modifier = Modifier,
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    ListBase(
        title = LocalStrings.current.disciplines,
        onCreateButtonClick = {
            val currentDestination = navController.getCurrentDestination()
            if (currentDestination !is DisciplineEditDestination || currentDestination.id != null) {
                showCreateDialog = true
            }
        },
        modifier = modifier,
    ) {
        when (state) {
            is DisciplineListViewModel.State.Loading -> LoadingIndicator()
            is DisciplineListViewModel.State.Loaded -> LoadedContent(
                navController = navController,
                state = state,
            )
        }
    }

    if (showCreateDialog) {
        CreateDisciplineDialog(
            onDismissRequest = {
                showCreateDialog = false
            },
            onCreateDiscipline = {
                showCreateDialog = false
                navController.navigate(DisciplineEditDestination()) {
                    popUpTo<DisciplineListDestination>()
                }
            },
            onCreateTeamDiscipline = {
                showCreateDialog = false
                navController.navigate(TeamDisciplineEditDestination()) {
                    popUpTo<DisciplineListDestination>()
                }
            },
        )
    }
}

@Composable
private fun LoadedContent(
    navController: NavController,
    state: DisciplineListViewModel.State.Loaded,
    modifier: Modifier = Modifier,
) {
    val disciplines by state.disciplineFlow.collectAsStateWithLifecycle()

    if (disciplines.isEmpty()) {
        val strings = LocalStrings.current

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = strings.xDontExist(strings.disciplines))
        }
    } else {
        val teamDisciplines by state.teamDisciplineFlow.collectAsStateWithLifecycle()
        val currentDestination by navController.currentDestinationAsState()
        val currentItemId by remember(navController) {
            derivedStateOf {
                when (val destination = currentDestination) {
                    is DisciplineDetailDestination -> destination.id
                    is DisciplineEditDestination -> destination.id
                    is TeamDisciplineDetailDestination -> destination.id
                    is TeamDisciplineEditDestination -> destination.id
                    else -> null
                }?.toObjectId()
            }
        }

        val itemModifier = Modifier.padding(2.dp)
        LazyColumn(
            modifier = modifier,
        ) {
            item(key = "disciplines", contentType = 0) {
                Text(
                    text = LocalStrings.current.disciplines,
                    modifier = Modifier.padding(start = 10.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            items(disciplines, key = { it.id }, contentType = { 1 }) { item ->
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
                    modifier = itemModifier,
                )
            }

            item(key = "team_disciplines", contentType = 2) {
                Text(
                    text = LocalStrings.current.teamDisciplines,
                    modifier = Modifier.padding(start = 10.dp, top = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            items(teamDisciplines, key = { it.id }, contentType = { 3 }) { item ->
                SelectableListItem(
                    headlineContent = { Text(item.name) },
                    selected = currentItemId == item.id,
                    onClick = {
                        if (currentItemId != item.id) {
                            navController.navigate(TeamDisciplineDetailDestination(item.id)) {
                                popUpTo<DisciplineListDestination>()
                            }
                        }
                    },
                    modifier = itemModifier,
                )
            }
        }
    }
}
