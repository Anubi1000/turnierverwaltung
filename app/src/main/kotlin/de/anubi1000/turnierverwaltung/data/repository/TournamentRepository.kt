package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Tournament
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface TournamentRepository {
    fun getAllAsFlow(): Flow<List<Tournament>>
    suspend fun getById(id: ObjectId): Tournament?
    suspend fun insert(tournament: Tournament)
    suspend fun update(tournament: Tournament)
    suspend fun delete(id: ObjectId)
}