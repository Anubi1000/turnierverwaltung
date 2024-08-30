package de.anubi1000.turnierverwaltung

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import cafe.adriel.lyricist.LocalStrings
import de.anubi1000.turnierverwaltung.data.repository.RepositoryModule
import de.anubi1000.turnierverwaltung.database.databaseModule
import de.anubi1000.turnierverwaltung.navigation.club.clubDetailDestinations
import de.anubi1000.turnierverwaltung.navigation.club.clubEditDestination
import de.anubi1000.turnierverwaltung.navigation.club.clubListDestination
import de.anubi1000.turnierverwaltung.navigation.participant.participantListDestination
import de.anubi1000.turnierverwaltung.navigation.team.teamListDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentListDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.tournamentDetailDestinations
import de.anubi1000.turnierverwaltung.navigation.tournament.tournamentEditDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.tournamentListDestination
import de.anubi1000.turnierverwaltung.server.ServerModule
import de.anubi1000.turnierverwaltung.server.ServerViewModel
import de.anubi1000.turnierverwaltung.ui.theme.AppTheme
import de.anubi1000.turnierverwaltung.viewmodel.ViewModelModule
import io.realm.kotlin.Realm
import org.apache.logging.log4j.kotlin.logger
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import java.awt.Dimension
import kotlin.system.exitProcess

private val log = logger("de.anubi1000.turnierverwaltung.MainKt")

@Composable
fun App() {
    AppTheme(darkTheme = false) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = TournamentListDestination,
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            tournamentListDestination(navController)
            tournamentDetailDestinations(navController)
            tournamentEditDestination(navController)

            participantListDestination(navController)

            teamListDestination(navController)

            clubListDestination(navController)
            clubDetailDestinations(navController)
            clubEditDestination(navController)
        }
    }
}

private fun appMain() {
    log.info("Hi from main")
    val koinApplication = startKoin {
        modules(databaseModule, RepositoryModule().module, ServerModule().module, ViewModelModule().module)

        createEagerInstances()
    }

    log.info("Starting webserver")
    val server: ServerViewModel by koinApplication.koin.inject()
    server.start()

    log.info("Starting application")
    application(exitProcessOnExit = false) {
        Window(
            onCloseRequest = ::exitApplication,
            title = LocalStrings.current.appName,
            icon = rememberVectorPainter(Icons.Default.Scoreboard),
        ) {
            val density = LocalDensity.current
            LaunchedEffect(Unit) {
                with(density) {
                    window.minimumSize = Dimension(
                        960.dp.roundToPx(),
                        540.dp.roundToPx()
                    )
                }
            }
            KoinContext {
                App()
            }
        }
    }

    log.info("Stopping webserver")
    server.stop()

    log.info("Saving realm database")
    val realm: Realm by koinApplication.koin.inject()
    realm.close()

    log.info("Bye bye")
}

fun main() {
    try {
        appMain()
    } catch (ex: Exception) {
        log.fatal("Exception occurred in main method", ex)
        exitProcess(1)
    }
}
