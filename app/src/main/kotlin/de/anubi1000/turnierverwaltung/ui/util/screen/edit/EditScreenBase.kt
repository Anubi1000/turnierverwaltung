package de.anubi1000.turnierverwaltung.ui.util.screen.edit

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
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
fun EditScreenBase(
    navController: NavController,
    title: String,
    onSaveButtonClick: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
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
                        }
                    )
                },
                actions = {
                    TooltipIconButton(
                        icon = Icons.Default.Save,
                        tooltip = LocalStrings.current.save,
                        enabled = onSaveButtonClick != null,
                        onClick = {
                            onSaveButtonClick?.invoke()
                        }
                    )
                }
            )
        },
        content = { padding ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                modifier = Modifier.padding(padding).padding(bottom = 8.dp, end = 8.dp).fillMaxSize()
            ) {
                content(PaddingValues(8.dp))
            }
        }
    )
}

