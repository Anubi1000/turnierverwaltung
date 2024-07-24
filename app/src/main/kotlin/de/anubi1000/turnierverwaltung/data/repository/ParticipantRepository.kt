package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.data.participant.ListParticipant
import de.anubi1000.turnierverwaltung.data.participant.Participant
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface ParticipantRepository {
   fun getAllAsFlow(tournamentId: ObjectId): Flow<ImmutableList<ListParticipant>>
   suspend fun getParticipantById(id: ObjectId): Participant?
   suspend fun deleteParticipant(id: ObjectId)
}