package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Club
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface ClubRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Club>>
    suspend fun getAllForTournament(tournamentId: ObjectId): List<Club>
    suspend fun getById(id: ObjectId): Club?
    suspend fun insert(club: Club, tournamentId: ObjectId)
    suspend fun update(club: Club)
    suspend fun delete(id: ObjectId)
}