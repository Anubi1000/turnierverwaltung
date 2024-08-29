package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.data.EditTournament
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
class TournamentRepositoryImpl(private val realm: Realm) : TournamentRepository {
    override fun getAllAsFlow(): Flow<List<Tournament>> {
        log.debug("Retrieving all tournaments as flow")
        return realm.query<Tournament>()
            .sort("date" to Sort.DESCENDING, "name" to Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getTournamentById(id: ObjectId): Tournament? {
        log.debug { "Querying tournament by id. id=${id.toHexString()}" }
        return realm.query<Tournament>("_id == $0", id)
            .first()
            .find()
    }

    override suspend fun insertTournament(tournament: Tournament) {
        log.debug { "Inserting new tournament. id=${tournament.id.toHexString()}" }
        realm.write {
            copyToRealm(tournament)
        }
    }

    override suspend fun updateTournament(editTournament: EditTournament) {
        log.debug { "Updating existing tournament. id=${editTournament.id.toHexString()}" }
        realm.write {
            val tournament = query<Tournament>("_id == $0", editTournament.id)
                .first()
                .find()!!

            tournament.name = editTournament.name
            tournament.date = editTournament.date
        }
    }

    override suspend fun deleteTournament(id: ObjectId) {
        log.debug { "Deleting tournament by id. id=${id.toHexString()}" }
        realm.write {
            val tournament = query<Tournament>("_id == $0", id)
                .first()
                .find()!!

            delete(tournament)
        }
    }

    companion object {
        private val log = logger()
    }
}