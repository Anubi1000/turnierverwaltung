package de.anubi1000.turnierverwaltung.navigation.tournament

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.tournament.TournamentEditScreen
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentEditViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.mongodb.kbson.ObjectId

@Serializable
data class TournamentEditDestination(
    val id: String?,
) : AppDestination {
    constructor(id: ObjectId? = null) : this(id?.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.TOURNAMENTS
}

fun NavGraphBuilder.tournamentEditDestination(
    navController: NavController,
) = composable<TournamentEditDestination> { backStackEntry ->
    val args: TournamentEditDestination = backStackEntry.toRoute()
    val viewModel: TournamentEditViewModel = koinViewModel()

    LaunchedEffect(viewModel) {
        if (args.id == null) {
            viewModel.loadCreate()
        } else {
            viewModel.loadEdit(args.id.toObjectId())
        }
    }

    TournamentEditScreen(
        navController = navController,
        state = viewModel.state,
        onSaveButtonClick = {
            viewModel.saveChanges {
                navController.navigate(TournamentDetailDestination(it)) {
                    popUpTo<TournamentListDestination>()
                }
            }
        },
        isEditMode = args.id != null,
    )
}
