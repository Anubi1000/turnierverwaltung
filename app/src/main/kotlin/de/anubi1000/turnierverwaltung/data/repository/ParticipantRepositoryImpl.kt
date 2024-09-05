package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Participant
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

@Factory
class ParticipantRepositoryImpl(private val realm: Realm) : ParticipantRepository {
    override fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Participant>> {
        log.debug("Retrieving all participants as flow")
        return realm.query<Participant>("tournament._id == $0", tournamentId)
            .sort("name", Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getAllForTournament(tournamentId: ObjectId): List<Participant> {
        return realm.query<Participant>("tournament._id == $0", tournamentId)
            .sort("name", Sort.ASCENDING)
            .find()
    }

    override suspend fun insert(participant: Participant, tournamentId: ObjectId) {
        withContext(Dispatchers.IO) {
            realm.write {
                require(participant.club != null) {
                    "Participant needs to have club"
                }
                participant.club = findLatest(participant.club!!)
                val dbParticipant = copyToRealm(participant)
                queryById<Tournament>(tournamentId)!!.participants.add(dbParticipant)
            }
        }
    }

    override suspend fun update(participant: Participant) {
        withContext(Dispatchers.IO) {
            realm.write {
                val dbParticipant = queryById<Participant>(participant.id)!!
                dbParticipant.name = participant.name
                dbParticipant.startNumber = participant.startNumber
                dbParticipant.gender = participant.gender
                dbParticipant.club = findLatest(participant.club!!)
            }
        }
    }

    override suspend fun getById(id: ObjectId): Participant? {
        return withContext(Dispatchers.IO) {
            realm.queryById(id)
        }
    }

    override suspend fun delete(id: ObjectId) {
        withContext(Dispatchers.IO) {
            realm.write {
                delete(queryById<Participant>(id)!!)
            }
        }
    }

    companion object {
        private val log = logger()
    }
}