package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Team
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface TeamRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Team>>
    suspend fun getTeamById(id: ObjectId): Team?
    suspend fun insertTeam(team: Team, tournamentId: ObjectId)
    suspend fun updateTeam(team: Team)
    suspend fun deleteTeamById(id: ObjectId)
}