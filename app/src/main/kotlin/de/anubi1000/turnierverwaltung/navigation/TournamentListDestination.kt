package de.anubi1000.turnierverwaltung.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.ui.shared.TournamentListLayout
import de.anubi1000.turnierverwaltung.util.topAppBarPadding
import kotlinx.serialization.Serializable

@Serializable
object TournamentListDestination : AppDestination {
    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.TOURNAMENTS
}

fun NavGraphBuilder.tournamentListDestination(navController: NavController) = composable<TournamentListDestination> {
    TournamentListLayout(
        navController = navController
    ) {
        Box(
            modifier = Modifier.fillMaxSize().topAppBarPadding(),
            contentAlignment = Alignment.Center
        ) {
            Text(LocalStrings.current.noTournamentSelected)
        }
    }
}