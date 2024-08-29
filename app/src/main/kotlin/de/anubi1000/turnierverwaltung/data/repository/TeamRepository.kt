package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Team
import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    fun getAllAsFlow(): Flow<List<Team>>
}