package de.anubi1000.turnierverwaltung.ui.discipline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.util.Icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDisciplineDialog(
    onDismissRequest: () -> Unit,
    onCreateDiscipline: () -> Unit,
    onCreateTeamDiscipline: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            color = AlertDialogDefaults.containerColor,
            shape = AlertDialogDefaults.shape,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(300.dp),
            ) {
                Text(
                    text = LocalStrings.current.newDiscipline,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 8.dp),
                )

                val listItemColors = ListItemDefaults.colors(
                    containerColor = AlertDialogDefaults.containerColor,
                )

                ListItem(
                    headlineContent = {
                        Text(LocalStrings.current.discipline)
                    },
                    trailingContent = {
                        Icon(Icons.Default.ChevronRight)
                    },
                    colors = listItemColors,
                    modifier = Modifier.clickable(onClick = onCreateDiscipline),
                )

                ListItem(
                    headlineContent = {
                        Text(LocalStrings.current.teamDiscipline)
                    },
                    trailingContent = {
                        Icon(Icons.Default.ChevronRight)
                    },
                    colors = listItemColors,
                    modifier = Modifier.clickable(onClick = onCreateTeamDiscipline),
                )

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End).padding(bottom = 16.dp, end = 16.dp),
                ) {
                    Text(LocalStrings.current.cancel)
                }
            }
        }
    }
}
