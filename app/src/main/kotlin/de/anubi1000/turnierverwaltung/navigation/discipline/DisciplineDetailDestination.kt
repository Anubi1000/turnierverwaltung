package de.anubi1000.turnierverwaltung.navigation.discipline

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.NavigationMenuOption
import de.anubi1000.turnierverwaltung.ui.discipline.DisciplineDetailScreen
import de.anubi1000.turnierverwaltung.util.toObjectId
import de.anubi1000.turnierverwaltung.viewmodel.discipline.DisciplineDetailViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.koin.compose.viewmodel.koinViewModel
import org.mongodb.kbson.ObjectId

@Serializable
data class DisciplineDetailDestination(
    val id: String,
) : AppDestination {
    constructor(id: ObjectId) : this(id.toHexString())

    @Transient
    override val navigationMenuOption: NavigationMenuOption = NavigationMenuOption.DISCIPLINES
}

fun NavGraphBuilder.disciplineDetailDestination(navController: NavController) = composable<DisciplineDetailDestination> { backStackEntry ->
    val args = backStackEntry.toRoute<DisciplineDetailDestination>()
    val viewModel: DisciplineDetailViewModel = koinViewModel()

    LaunchedEffect(viewModel) {
        viewModel.loadItem(args.id.toObjectId())
    }

    DisciplineDetailScreen(
        navController = navController,
        state = viewModel.state,
        onDeleteButtonClick = {
            viewModel.deleteItem {
                navController.popBackStack()
            }
        },
    )
}
