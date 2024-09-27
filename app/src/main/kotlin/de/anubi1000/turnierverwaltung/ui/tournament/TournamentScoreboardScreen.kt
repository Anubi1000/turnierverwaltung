package de.anubi1000.turnierverwaltung.ui.tournament

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.ScoreboardData
import de.anubi1000.turnierverwaltung.ui.util.LoadingIndicator
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentScoreboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentScoreboardScreen(
    navController: NavController,
    state: TournamentScoreboardViewModel.State,
    onSaveButtonClick: (Int) -> Unit,
) {
    var selectedIndex by remember(state) { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LocalStrings.current.overview) },
                actions = {
                    TooltipIconButton(
                        icon = Icons.Default.Save,
                        tooltip = LocalStrings.current.save,
                        onClick = { onSaveButtonClick(selectedIndex) },
                        enabled = state is TournamentScoreboardViewModel.State.Loaded && state.data.tables.isNotEmpty(),
                    )
                },
            )
        },
    ) { padding ->
        when (state) {
            TournamentScoreboardViewModel.State.Loading -> LoadingIndicator()
            is TournamentScoreboardViewModel.State.Loaded -> LoadedContent(
                state = state,
                selectedIndex = selectedIndex,
                onSelectedIndexChange = { selectedIndex = it },
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun LoadedContent(
    state: TournamentScoreboardViewModel.State.Loaded,
    selectedIndex: Int,
    onSelectedIndexChange: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.data.tables.isNotEmpty()) {
        Column(
            modifier = modifier,
        ) {
            TabRow(
                state = state,
                selectedIndex = selectedIndex,
                onSelectedIndexChange = onSelectedIndexChange,
            )

            val table = state.data.tables[selectedIndex]

            ScoreboardContent(table)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabRow(
    state: TournamentScoreboardViewModel.State.Loaded,
    selectedIndex: Int,
    onSelectedIndexChange: (index: Int) -> Unit,
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        tabs = {
            state.data.tables.forEachIndexed { index, table ->
                Tab(
                    selected = index == selectedIndex,
                    content = {
                        Text(
                            text = table.name,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    },
                    onClick = { onSelectedIndexChange(index) },
                    modifier = Modifier.height(48.dp),
                )
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScoreboardContent(
    table: ScoreboardData.Table,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        stickyHeader {
            Surface {
                Column {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth().height(40.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        table.columns.forEach { column ->
                            val textModifier = when (val width = column.width) {
                                is ScoreboardData.Table.Column.Width.Fixed -> Modifier.width(width.toDp())
                                is ScoreboardData.Table.Column.Width.Variable -> Modifier.weight(
                                    width.weight,
                                    fill = true,
                                )
                            }.padding(4.dp)

                            Text(
                                text = column.name,
                                modifier = textModifier,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = column.alignment.toComposeTextAlignment(),
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        }

        itemsIndexed(table.rows) { rowIndex, row ->
            Surface(
                color = if (rowIndex % 2 == 1) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.background,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth().height(40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    table.columns.forEachIndexed { cellIndex, column ->
                        val textModifier = when (val width = column.width) {
                            is ScoreboardData.Table.Column.Width.Fixed -> Modifier.width(width.toDp())
                            is ScoreboardData.Table.Column.Width.Variable -> Modifier.weight(
                                width.weight,
                                fill = true,
                            )
                        }.padding(4.dp)

                        Text(
                            text = row.values[cellIndex],
                            style = MaterialTheme.typography.titleSmall,
                            textAlign = column.alignment.toComposeTextAlignment(),
                            modifier = textModifier,
                        )
                    }
                }
            }
        }
    }
}
