package de.anubi1000.turnierverwaltung.ui.util.screen.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListBase(
    title: String,
    modifier: Modifier = Modifier,
    onCreateButtonClick: (() -> Unit)? = null,
    searchValueFlow: MutableStateFlow<String>? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            var searchEnabled by remember { mutableStateOf(false) }

            if (searchEnabled && searchValueFlow != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = SearchBarDefaults.inputFieldShape,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp).height(56.dp),
                ) {
                    val searchValue by searchValueFlow.collectAsStateWithLifecycle()

                    SearchBarDefaults.InputField(
                        query = searchValue,
                        onQueryChange = { searchValueFlow.value = it },
                        onSearch = {},
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text(LocalStrings.current.searchTerm) },
                        trailingIcon = {
                            TooltipIconButton(
                                icon = Icons.Default.Close,
                                tooltip = LocalStrings.current.back,
                                onClick = {
                                    searchEnabled = false
                                    searchValueFlow.value = ""
                                },
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                TopAppBar(
                    title = { Text(title) },
                    actions = {
                        onCreateButtonClick?.let { onClick ->
                            TooltipIconButton(
                                icon = Icons.Default.Add,
                                tooltip = LocalStrings.current.create,
                                onClick = onClick,
                            )
                        }
                        if (searchValueFlow != null) {
                            TooltipIconButton(
                                icon = Icons.Default.Search,
                                tooltip = LocalStrings.current.search,
                                onClick = {
                                    searchEnabled = true
                                },
                            )
                        }
                    },
                )
            }
        },
        modifier = modifier,
        content = content,
    )
}
