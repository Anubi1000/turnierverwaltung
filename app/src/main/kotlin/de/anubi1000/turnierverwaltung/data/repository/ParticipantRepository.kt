package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.database.queryById
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.kotlin.logger
import org.koin.core.annotation.Factory
import org.mongodb.kbson.ObjectId

interface ParticipantRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Participant>>
    suspend fun getById(id: ObjectId): Participant?
    suspend fun insert(participant: Participant, tournamentId: ObjectId)
    suspend fun update(participant: Participant)
    suspend fun updateResult(participantId: ObjectId, disciplineId: ObjectId, result: Participant.DisciplineResult)
    suspend fun delete(id: ObjectId)
}

@Factory
class ParticipantRepositoryImpl(private val realm: Realm) : ParticipantRepository {
    override fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Participant>> {
        log.debug { "Retrieving all participants for tournament(${tournamentId.toHexString()})" }

        return realm.query<Participant>("tournament._id == $0", tournamentId)
            .sort("startNumber" to Sort.ASCENDING, "name" to Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getById(id: ObjectId): Participant? {
        log.debug { "Retrieving participant by id(${id.toHexString()})" }

        return withContext(Dispatchers.IO) {
            realm.queryById(id)
        }
    }

    override suspend fun insert(participant: Participant, tournamentId: ObjectId) {
        log.debug { "Inserting new participant with id(${participant.id.toHexString()}) for tournament(${tournamentId.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val tournament = queryById<Tournament>(tournamentId) ?: throw IllegalArgumentException("Tournament with with specified id not found")

                val club = participant.club?.let(::findLatest) ?: throw IllegalArgumentException("Participant needs to have a valid club")

                participant.club = club
                val dbParticipant = copyToRealm(participant)
                tournament.participants.add(dbParticipant)
            }
        }

        log.trace { "Inserted participant with id(${participant.id.toHexString()})" }
    }

    override suspend fun update(participant: Participant) {
        log.debug { "Updating participant with id(${participant.id.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val dbParticipant = queryById<Participant>(participant.id) ?: throw IllegalArgumentException("Participant with specified id not found")
                val club = participant.club?.let(::findLatest) ?: throw IllegalArgumentException("Participant needs to have a valid club")

                dbParticipant.name = participant.name
                dbParticipant.startNumber = participant.startNumber
                dbParticipant.gender = participant.gender
                dbParticipant.club = club
            }
        }

        log.trace { "Updated participant with id(${participant.id.toHexString()})" }
    }

    override suspend fun updateResult(
        participantId: ObjectId,
        disciplineId: ObjectId,
        result: Participant.DisciplineResult,
    ) {
        log.debug { "Updating results for participant with id(${participantId.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val dbParticipant = queryById<Participant>(participantId) ?: throw IllegalArgumentException("Participant with specified id not found")
                dbParticipant.results[disciplineId.toHexString()] = result
            }
        }

        log.trace { "Updated results for participant with id(${participantId.toHexString()})" }
    }

    override suspend fun delete(id: ObjectId) {
        log.debug { "Deleting participant with id(${id.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val participant = queryById<Participant>(id)

                if (participant != null) {
                    require(query<Team>("ANY members._id == $0", participant.id).count().find() == 0L) {
                        "Participant with specified id is used in teams"
                    }

                    delete(participant)
                    log.trace { "Participant with id(${id.toHexString()}) deleted" }
                } else {
                    log.warn { "Participant with id ${id.toHexString()} doesn't exist" }
                }
            }
        }
    }

    companion object {
        private val log = logger()
    }
}
