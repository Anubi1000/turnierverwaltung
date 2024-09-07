package de.anubi1000.turnierverwaltung.data.repository

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

interface TournamentRepository {
    fun getAllAsFlow(): Flow<List<Tournament>>
    suspend fun getById(id: ObjectId): Tournament?
    suspend fun insert(tournament: Tournament)
    suspend fun update(tournament: Tournament)
    suspend fun delete(id: ObjectId)
}

@Factory
class TournamentRepositoryImpl(private val realm: Realm) : TournamentRepository {
    override fun getAllAsFlow(): Flow<List<Tournament>> {
        log.debug("Retrieving all tournaments as flow")
        return realm.query<Tournament>()
            .sort("date" to Sort.DESCENDING, "name" to Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getById(id: ObjectId): Tournament? {
        log.debug { "Querying tournament by id(${id.toHexString()})" }
        return withContext(Dispatchers.IO) {
            realm.queryById(id)
        }
    }

    override suspend fun insert(tournament: Tournament) {
        log.debug { "Inserting new tournament with id(${tournament.id.toHexString()})" }
        withContext(Dispatchers.IO) {
            realm.write {
                copyToRealm(tournament)
            }
        }
    }

    override suspend fun update(tournament: Tournament) {
        log.debug { "Updating existing tournament with id(${tournament.id.toHexString()})" }
        withContext(Dispatchers.IO) {
            realm.write {
                val databaseTournament = queryById<Tournament>(tournament.id)!!

                databaseTournament.name = tournament.name
                databaseTournament.date = tournament.date
            }
        }
    }

    override suspend fun delete(id: ObjectId) {
        log.debug { "Deleting tournament by id(${id.toHexString()})" }
        withContext(Dispatchers.IO) {
            realm.write {
                val dbTournament = queryById<Tournament>(id)!!

                delete(dbTournament.clubs)
                delete(dbTournament.participants)
                delete(dbTournament.teams)
                delete(dbTournament.disciplines)

                delete(dbTournament)
            }
        }
    }

    companion object {
        private val log = logger()
    }
}