package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Club
import kotlinx.coroutines.flow.Flow

interface ClubRepository {
    fun getAllAsFlow(): Flow<List<Club>>
}