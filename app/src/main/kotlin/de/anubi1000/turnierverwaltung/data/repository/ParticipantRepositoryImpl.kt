package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.data.participant.ListParticipant
import de.anubi1000.turnierverwaltung.data.participant.Participant
import de.anubi1000.turnierverwaltung.data.participant.toListParticipant
import de.anubi1000.turnierverwaltung.data.participant.toParticipant
import de.anubi1000.turnierverwaltung.database.model.ParticipantModel
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.apache.logging.log4j.kotlin.logger
import org.mongodb.kbson.ObjectId

class ParticipantRepositoryImpl(private val realm: Realm) : ParticipantRepository {
    override fun getAllAsFlow(tournamentId: ObjectId): Flow<ImmutableList<ListParticipant>> {
        log.debug { "Getting participants as flow for tournament ${tournamentId.toHexString()}" }
        return realm.query<ParticipantModel>("tournament._id == $0", tournamentId)
            .sort("name" to Sort.ASCENDING)
            .asFlow()
            .map { resultsChange ->
                resultsChange.list.map(ParticipantModel::toListParticipant).toImmutableList()
            }
    }

    override suspend fun getParticipantById(id: ObjectId): Participant? {
        log.debug { "Getting participant with id ${id.toHexString()}" }
        return realm
            .query<ParticipantModel>("_id == $0")
            .asFlow()
            .first()
            .list
            .firstOrNull()
            ?.toParticipant()
    }

    override suspend fun deleteParticipant(id: ObjectId) {
        log.debug { "Deleting participant with id ${id.toHexString()}" }
        realm.write {
            query<ParticipantModel>("_id == $0", id)
                .find()
                .first()
                .also(::delete)
        }
    }

    companion object {
        private val log = logger()
    }
}