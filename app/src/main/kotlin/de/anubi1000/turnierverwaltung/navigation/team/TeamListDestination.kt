package de.anubi1000.turnierverwaltung.navigation.team

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.shared.TournamentNavigationLayout
import de.anubi1000.turnierverwaltung.ui.shared.list.TeamListLayout
import de.anubi1000.turnierverwaltung.util.topAppBarPadding
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data object TeamListDestination : AppDestination {
    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.TEAMS
}

fun NavGraphBuilder.teamListDestination(navController: NavController) = composable<TeamListDestination> {
    TournamentNavigationLayout(navController) {
        TeamListLayout(navController) {
            Box(
                modifier = Modifier.fillMaxSize().topAppBarPadding(),
                contentAlignment = Alignment.Center
            ) {
                Text(LocalStrings.current.noTeamSelected)
            }
        }
    }
}