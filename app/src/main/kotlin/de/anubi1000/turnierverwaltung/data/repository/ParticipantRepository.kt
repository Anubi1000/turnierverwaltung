package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Participant
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface ParticipantRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Participant>>
    suspend fun getParticipantById(id: ObjectId): Participant?
    suspend fun insertParticipant(participant: Participant, tournamentId: ObjectId)
    suspend fun updateParticipant(participant: Participant)
}