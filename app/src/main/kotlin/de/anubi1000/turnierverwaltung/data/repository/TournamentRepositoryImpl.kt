package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.queryById
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
        log.debug { "Querying tournament by id(${id.toHexString()})" }
        return realm.queryById(id)
    }

    override suspend fun insertTournament(tournament: Tournament) {
        log.debug { "Inserting new tournament with id(${tournament.id.toHexString()})" }
        realm.write {
            copyToRealm(tournament)
        }
    }

    override suspend fun updateTournament(tournament: Tournament) {
        log.debug { "Updating existing tournament with id(${tournament.id.toHexString()})" }
        realm.write {
            val databaseTournament = queryById<Tournament>(tournament.id)!!

            databaseTournament.name = tournament.name
            databaseTournament.date = tournament.date
        }
    }

    override suspend fun deleteTournament(id: ObjectId) {
        log.debug { "Deleting tournament by id(${id.toHexString()})" }
        realm.write {
            val tournament = queryById<Tournament>(id)!!

            delete(tournament.clubs)

            delete(tournament)
        }
    }

    companion object {
        private val log = logger()
    }
}