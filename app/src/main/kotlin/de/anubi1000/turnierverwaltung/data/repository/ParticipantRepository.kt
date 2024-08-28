package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Participant
import kotlinx.coroutines.flow.Flow

interface ParticipantRepository {
    fun getAllAsFlow(): Flow<List<Participant>>
}