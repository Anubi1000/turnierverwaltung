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
        log.debug("Retrieving all tournaments")

        return realm.query<Tournament>()
            .sort("date" to Sort.DESCENDING, "name" to Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getById(id: ObjectId): Tournament? {
        log.debug { "Retrieving tournament by id(${id.toHexString()})" }

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

        log.trace { "Inserted tournament with id(${tournament.id.toHexString()})" }
    }

    override suspend fun update(tournament: Tournament) {
        log.debug { "Updating existing tournament with id(${tournament.id.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val databaseTournament = queryById<Tournament>(tournament.id) ?: throw IllegalArgumentException("Tournament with specified id not found")

                databaseTournament.name = tournament.name
                databaseTournament.date = tournament.date
            }
        }

        log.trace { "Updated tournament with id(${tournament.id.toHexString()})" }
    }

    override suspend fun delete(id: ObjectId) {
        log.debug { "Deleting tournament by id(${id.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val tournament = queryById<Tournament>(id)

                if (tournament != null) {
                    delete(tournament.clubs)
                    delete(tournament.participants)
                    delete(tournament.teams)
                    delete(tournament.disciplines)
                    delete(tournament.teamDisciplines)

                    delete(tournament)

                    log.trace { "Tournament with id(${id.toHexString()}) deleted" }
                } else {
                    log.warn { "Tournament with id(${id.toHexString()}) doesn't exist" }
                }
            }
        }
    }

    companion object {
        private val log = logger()
    }
}
