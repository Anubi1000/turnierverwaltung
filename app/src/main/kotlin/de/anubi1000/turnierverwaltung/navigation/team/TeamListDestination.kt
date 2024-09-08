package de.anubi1000.turnierverwaltung.navigation.team

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.util.CenteredText
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data object TeamListDestination : AppDestination {
    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.TEAMS
}

fun NavGraphBuilder.teamListDestination(navController: NavController) = composable<TeamListDestination> {
    CenteredText(
        text = LocalStrings.current.noTeamSelected,
    )
}
