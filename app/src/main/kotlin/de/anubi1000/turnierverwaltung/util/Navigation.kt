package de.anubi1000.turnierverwaltung.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.bundle.Bundle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.serialization.decodeArguments
import androidx.navigation.toRoute
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.club.ClubDetailDestination
import de.anubi1000.turnierverwaltung.navigation.club.ClubEditDestination
import de.anubi1000.turnierverwaltung.navigation.club.ClubListDestination
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantDetailDestination
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantEditDestination
import de.anubi1000.turnierverwaltung.navigation.participant.ParticipantListDestination
import de.anubi1000.turnierverwaltung.navigation.team.TeamDetailDestination
import de.anubi1000.turnierverwaltung.navigation.team.TeamEditDestination
import de.anubi1000.turnierverwaltung.navigation.team.TeamListDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentEditDestination
import de.anubi1000.turnierverwaltung.navigation.tournament.TournamentListDestination
import de.anubi1000.turnierverwaltung.ui.team.edit.TeamEditScreen
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@OptIn(InternalSerializationApi::class)
private val destinations: Map<Int, KClass<out AppDestination>> = listOf(
    TournamentListDestination::class,
    TournamentDetailDestination::class,
    TournamentEditDestination::class,

    ParticipantListDestination::class,
    ParticipantDetailDestination::class,
    ParticipantEditDestination::class,

    TeamListDestination::class,
    TeamDetailDestination::class,
    TeamEditDestination::class,

    ClubListDestination::class,
    ClubDetailDestination::class,
    ClubEditDestination::class
).associateBy { it.serializer().hashCode() }

fun NavController.getCurrentDestination(): AppDestination? = getDestination(currentBackStackEntry)

@Composable
fun NavController.currentDestinationAsState(): State<AppDestination?> {
    val backStackEntry by currentBackStackEntryAsState()
    return remember(this) {
        derivedStateOf {
            getDestination(backStackEntry)
        }
    }
}

inline fun <reified T : AppDestination> NavController.getDestination(): T {
    return getBackStackEntry<T>().toRoute<T>()
}

@OptIn(InternalSerializationApi::class)
private fun getDestination(backStackEntry: NavBackStackEntry?): AppDestination? {
    if (backStackEntry == null) return null

    val id = backStackEntry.destination.id
    val destinationClass = destinations[id] ?: return null

    val arguments = backStackEntry.arguments ?: Bundle()
    val typeMap = backStackEntry.destination.arguments.mapValues { it.value.type }
    return destinationClass.serializer().decodeArguments(arguments, typeMap)
}