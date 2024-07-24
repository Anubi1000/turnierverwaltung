package de.anubi1000.turnierverwaltung.navigation.tournament

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.anubi1000.turnierverwaltung.navigation.AppScreen
import de.anubi1000.turnierverwaltung.navigation.NavigationListType
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuType
import de.anubi1000.turnierverwaltung.ui.tournament.edit.TournamentEditScreen
import de.anubi1000.turnierverwaltung.viewmodel.TournamentEditViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.mongodb.kbson.ObjectId

class TournamentEditDestination(val tournamentId: ObjectId) : AppScreen {
    override val navigationMenuType: NavigationMenuType = NavigationMenuType.TOP_LEVEL
    override val navigationListType: NavigationListType = NavigationListType.TOURNAMENTS

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: TournamentEditViewModel = koinViewModel()

        LaunchedEffect(viewModel) {
            viewModel.loadEdit(tournamentId)
        }

        TournamentEditScreen(
            state = viewModel.state,
            isEditMode = true,
            onSaveButtonClick = {
                viewModel.saveChanges(
                    onSaved = {
                        navigator.pop()
                    }
                )
            }
        )
    }
}