package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.queryById
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Tournament
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun insertParticipant(participant: Participant, tournamentId: ObjectId) {
        realm.write {
            participant.club = findLatest(participant.club!!)
            val dbParticipant = copyToRealm(participant)
            queryById<Tournament>(tournamentId)!!.participants.add(dbParticipant)
        }
    }

    override suspend fun updateParticipant(participant: Participant) {
        realm.write {
            val dbParticipant = queryById<Participant>(participant.id)!!
            dbParticipant.name = participant.name
            dbParticipant.gender = participant.gender
            dbParticipant.club = findLatest(participant.club!!)
        }
    }

    override suspend fun getParticipantById(id: ObjectId): Participant? {
        return realm.queryById(id)
    }

    companion object {
        private val log = logger()
    }
}