package de.anubi1000.turnierverwaltung.navigation.tournament

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.voyager.core.screen.ScreenKey
import de.anubi1000.turnierverwaltung.navigation.AppScreen
import de.anubi1000.turnierverwaltung.navigation.NavigationListType
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuType
import de.anubi1000.turnierverwaltung.navigation.screenKey

class TournamentListDestination : AppScreen {
    override val navigationMenuType: NavigationMenuType = NavigationMenuType.TOP_LEVEL
    override val navigationListType: NavigationListType = NavigationListType.TOURNAMENTS

    override val key: ScreenKey = screenKey()

    @Composable
    override fun Content() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(LocalStrings.current.noTournamentSelected)
            }
        }
    }
}