package de.anubi1000.turnierverwaltung.ui.util.screen.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListBase(
    title: String,
    modifier: Modifier = Modifier,
    onCreateButtonClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
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
                },
            )
        },
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier.padding(it).fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content,
        )
    }
}
