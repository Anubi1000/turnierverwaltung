package de.anubi1000.turnierverwaltung

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Scoreboard
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.lyricist.LocalStrings
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import de.anubi1000.turnierverwaltung.data.repository.repositoryModule
import de.anubi1000.turnierverwaltung.database.databaseModule
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentListDestination
import de.anubi1000.turnierverwaltung.ui.app.NavigationList
import de.anubi1000.turnierverwaltung.ui.app.NavigationMenu
import de.anubi1000.turnierverwaltung.ui.theme.AppTheme
import de.anubi1000.turnierverwaltung.viewmodel.viewModelModule
import io.realm.kotlin.Realm
import org.apache.logging.log4j.kotlin.logger
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import kotlin.system.exitProcess

private val log = logger("de.anubi1000.turnierverwaltung.MainKt")

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun App() {
    AppTheme(darkTheme = false) {
        ProvideNavigatorLifecycleKMPSupport {
            Navigator(TournamentListDestination()) {
                Row {
                    NavigationMenu()
                    NavigationList()
                    CurrentScreen()
                }
            }
        }
    }
}

private fun appMain() {
    log.info("Hi from main")
    val koinApplication = startKoin {
        modules(databaseModule, repositoryModule, viewModelModule)

        createEagerInstances()
    }

    //log.info("Starting server")
    //val server: ServerViewModel by di.instance()
    //server.start()

    log.info("Starting application")
    application(exitProcessOnExit = false) {
        Window(
            onCloseRequest = ::exitApplication,
            title = LocalStrings.current.appName,
            icon = rememberVectorPainter(Icons.Default.Scoreboard),
        ) {
            KoinContext {
                App()
            }
        }
    }

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
