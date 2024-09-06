package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Team
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface TeamRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Team>>
    suspend fun getById(id: ObjectId): Team?
    suspend fun insert(team: Team, tournamentId: ObjectId)
    suspend fun update(team: Team)
    suspend fun delete(id: ObjectId)
}