package de.anubi1000.turnierverwaltung.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.anubi1000.turnierverwaltung.navigation.AppScreen
import de.anubi1000.turnierverwaltung.navigation.NavigationListType

@Composable
fun NavigationList() {
    val navigator = LocalNavigator.currentOrThrow
    val screen = navigator.lastItem as AppScreen

    when (screen.navigationListType) {
        NavigationListType.TOURNAMENTS -> Row {
            TournamentList()
            Spacer(Modifier.width(24.dp).fillMaxHeight().background(MaterialTheme.colorScheme.background))
        }
        else -> {}
    }
}