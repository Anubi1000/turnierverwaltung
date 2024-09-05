package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Participant
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface ParticipantRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Participant>>
    suspend fun getAllForTournament(tournamentId: ObjectId): List<Participant>
    suspend fun getById(id: ObjectId): Participant?
    suspend fun insert(participant: Participant, tournamentId: ObjectId)
    suspend fun update(participant: Participant)
    suspend fun updateResult(participantId: ObjectId, disciplineId: ObjectId, result: Participant.DisciplineResult)
    suspend fun delete(id: ObjectId)
}