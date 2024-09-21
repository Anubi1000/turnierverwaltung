package de.anubi1000.turnierverwaltung.ui.util.screen.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.TooltipIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenBase(
    navController: NavController,
    title: String,
    onEditButtonClick: (() -> Unit)? = null,
    onDeleteButtonClick: (() -> Unit)? = null,
    additionalActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    TooltipIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        tooltip = LocalStrings.current.back,
                        onClick = {
                            navController.popBackStack()
                        },
                    )
                },
                actions = {
                    additionalActions()

                    TooltipIconButton(
                        icon = Icons.Default.Delete,
                        tooltip = LocalStrings.current.delete,
                        enabled = onDeleteButtonClick != null,
                        onClick = { onDeleteButtonClick?.invoke() },
                    )

                    onEditButtonClick?.let { onClick ->
                        TooltipIconButton(
                            icon = Icons.Default.Edit,
                            tooltip = LocalStrings.current.edit,
                            onClick = onClick,
                        )
                    }
                },
            )
        },
        floatingActionButton = floatingActionButton,
        content = { padding ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                modifier = Modifier.padding(padding).padding(bottom = 8.dp, end = 8.dp).fillMaxSize(),
            ) {
                content(PaddingValues(8.dp))
            }
        },
    )
}
