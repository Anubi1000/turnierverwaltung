package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Club
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface ClubRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Club>>
    suspend fun getAllForTournament(tournamentId: ObjectId): List<Club>
    suspend fun getClubById(id: ObjectId): Club?
    suspend fun insertClub(club: Club, tournamentId: ObjectId)
    suspend fun updateClub(club: Club)
    suspend fun deleteClub(id: ObjectId)
}