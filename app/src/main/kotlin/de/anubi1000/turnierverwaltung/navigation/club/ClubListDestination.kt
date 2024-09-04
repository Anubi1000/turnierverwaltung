package de.anubi1000.turnierverwaltung.navigation.club

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.util.CenteredText
import de.anubi1000.turnierverwaltung.util.topAppBarPadding
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data object ClubListDestination : AppDestination {
    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.CLUBS
}

fun NavGraphBuilder.clubListDestination(navController: NavController) = composable<ClubListDestination> {
    CenteredText(
        text = LocalStrings.current.noClubSelected,
        modifier = Modifier.topAppBarPadding()
    )
}