package de.anubi1000.turnierverwaltung.navigation.tournament

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.ScreenKey
import de.anubi1000.turnierverwaltung.navigation.AppScreen
import de.anubi1000.turnierverwaltung.navigation.NavigationListType
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuType
import de.anubi1000.turnierverwaltung.navigation.screenKey
import de.anubi1000.turnierverwaltung.ui.tournament.detail.TournamentDetailScreen
import de.anubi1000.turnierverwaltung.viewmodel.TournamentDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.mongodb.kbson.ObjectId

class TournamentDetailDestination(val tournamentId: ObjectId) : AppScreen {
    override val navigationMenuType: NavigationMenuType = NavigationMenuType.TOP_LEVEL
    override val navigationListType: NavigationListType = NavigationListType.TOURNAMENTS

    override val key: ScreenKey = screenKey(tournamentId)

    @Composable
    override fun Content() {
        val viewModel: TournamentDetailViewModel = koinViewModel()

        LaunchedEffect(viewModel) {
            viewModel.loadTournament(tournamentId)
        }

        TournamentDetailScreen(
            state = viewModel.state,
            deleteTournament = { viewModel.deleteTournament() }
        )
    }
}