package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.data.tournament.EditTournament
import de.anubi1000.turnierverwaltung.database.model.Tournament
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface TournamentRepository {
    fun getAllAsFlow(): Flow<List<Tournament>>
    suspend fun getTournamentById(id: ObjectId): Tournament?
    suspend fun insertTournament(tournament: Tournament)
    suspend fun updateTournament(editTournament: EditTournament)
    suspend fun deleteTournament(id: ObjectId)
}