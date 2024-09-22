package de.anubi1000.turnierverwaltung

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import de.anubi1000.turnierverwaltung.ui.util.getCurrentDestination
import org.apache.logging.log4j.kotlin.logger

@Stable
class AppState(val navController: NavHostController) {
    init {
        navController.addOnDestinationChangedListener { navController, _, _ ->
            log.debug { "Navigated to ${navController.getCurrentDestination()}" }
        }
    }

    companion object {
        private val log = logger()
    }
}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()): AppState = remember(
    navController,
) {
    AppState(
        navController = navController,
    )
}
