package de.anubi1000.turnierverwaltung.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.bundle.Bundle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.serialization.decodeArguments
import de.anubi1000.turnierverwaltung.navigation.AppDestination
import de.anubi1000.turnierverwaltung.navigation.TournamentDetailDestination
import de.anubi1000.turnierverwaltung.navigation.TournamentEditDestination
import de.anubi1000.turnierverwaltung.navigation.TournamentListDestination
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@OptIn(InternalSerializationApi::class)
private val destinations: Map<Int, KClass<out AppDestination>> = listOf(
    TournamentEditDestination::class,
    TournamentDetailDestination::class,
    TournamentListDestination::class
).associateBy { it.serializer().hashCode() }

@OptIn(InternalSerializationApi::class)
fun NavController.getCurrentDestination(): AppDestination? {
    val backStackEntry = currentBackStackEntry ?: return null

    val id = backStackEntry.destination.id
    val destinationClass = destinations[id] ?: return null

    val arguments = backStackEntry.arguments ?: Bundle()
    val typeMap = backStackEntry.destination.arguments.mapValues { it.value.type }
    return destinationClass.serializer().decodeArguments(arguments, typeMap)
}

@OptIn(InternalSerializationApi::class)
@Composable
fun NavController.currentDestinationAsState(): State<AppDestination?> {
    val backStackEntry by currentBackStackEntryAsState()
    return remember(this) {
        derivedStateOf {
            val entry = backStackEntry ?: return@derivedStateOf null

            val id = entry.destination.id
            val destinationClass = destinations[id] ?: return@derivedStateOf null

            val arguments = entry.arguments ?: Bundle()
            val typeMap = entry.destination.arguments.mapValues { it.value.type }
            return@derivedStateOf destinationClass.serializer().decodeArguments(arguments, typeMap)
        }
    }
}