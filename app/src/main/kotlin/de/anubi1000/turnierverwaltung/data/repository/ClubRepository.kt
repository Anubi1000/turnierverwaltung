package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.database.queryById
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.kotlin.logger
import org.koin.core.annotation.Factory
import org.mongodb.kbson.ObjectId

interface ClubRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Club>>
    suspend fun getAllForTournament(tournamentId: ObjectId): List<Club>
    suspend fun getById(id: ObjectId): Club?
    suspend fun insert(club: Club, tournamentId: ObjectId)
    suspend fun update(club: Club)
    suspend fun delete(id: ObjectId)
}

@Factory
class ClubRepositoryImpl(private val realm: Realm) : ClubRepository {
    override fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Club>> {
        log.debug { "Retrieving all clubs for tournament(${tournamentId.toHexString()}) as flow" }
        return realm.query<Club>("tournament._id == $0", tournamentId)
            .sort("name", Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getAllForTournament(tournamentId: ObjectId): List<Club> {
        log.debug { "Retrieving all clubs for tournament(${tournamentId.toHexString()})" }
        return withContext(Dispatchers.IO) {
            getAllForTournamentAsFlow(tournamentId).first()
        }
    }

    override suspend fun getById(id: ObjectId): Club? {
        log.debug { "Retrieving club with id(${id.toHexString()})" }
        return withContext(Dispatchers.IO) {
            realm.queryById(id)
        }
    }

    override suspend fun insert(club: Club, tournamentId: ObjectId) {
        log.debug { "Inserting new club with id(${club.id.toHexString()}) for tournament(${tournamentId.toHexString()})" }
        withContext(Dispatchers.IO) {
            realm.write {
                val tournament = queryById<Tournament>(tournamentId) ?: throw IllegalArgumentException("Specified tournament does not exist")

                val insertedClub = copyToRealm(club)
                tournament.clubs.add(insertedClub)
            }
        }
    }

    override suspend fun update(club: Club) {
        log.debug { "Updating club with id(${club.id.toHexString()})" }
        withContext(Dispatchers.IO) {
            realm.write {
                val databaseClub = queryById<Club>(club.id) ?: throw IllegalArgumentException("Specified club does not exist")

                databaseClub.name = club.name
            }
        }
    }

    override suspend fun delete(id: ObjectId) {
        log.debug { "Deleting club with id(${id.toHexString()})" }
        withContext(Dispatchers.IO) {
            realm.write {
                queryById<Club>(id)?.let(::delete)
            }
        }
    }

    companion object {
        private val log = logger()
    }
}
