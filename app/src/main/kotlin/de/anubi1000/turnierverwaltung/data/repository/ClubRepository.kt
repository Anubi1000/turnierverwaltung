package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.database.model.Participant
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
    suspend fun getById(id: ObjectId): Club?
    suspend fun insert(club: Club, tournamentId: ObjectId)
    suspend fun update(club: Club)
    suspend fun delete(id: ObjectId)

    suspend fun getParticipantsWithClub(id: ObjectId): Int
}

@Factory
class ClubRepositoryImpl(private val realm: Realm) : ClubRepository {
    override fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Club>> {
        log.debug { "Querying all clubs for tournament(${tournamentId.toHexString()}) as flow" }

        return realm.query<Club>("tournament._id == $0", tournamentId)
            .sort("name", Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getById(id: ObjectId): Club? {
        log.debug { "Querying club with id(${id.toHexString()})" }

        return withContext(Dispatchers.IO) {
            realm.queryById(id)
        }
    }

    override suspend fun insert(club: Club, tournamentId: ObjectId) {
        log.debug { "Inserting new club with id(${club.id.toHexString()}) for tournament(${tournamentId.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val tournament = queryById<Tournament>(tournamentId)
                require(tournament != null) {
                    "Tournament with id(${tournamentId.toHexString()}) doesn't exist"
                }

                val insertedClub = copyToRealm(club)
                tournament.clubs.add(insertedClub)
            }
        }

        log.trace { "Inserted club with id(${club.id.toHexString()})" }
    }

    override suspend fun update(club: Club) {
        log.debug { "Updating club with id(${club.id.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val dbClub = queryById<Club>(club.id)
                require(dbClub != null) {
                    "Club with id(${club.id.toHexString()}) doesn't exist"
                }

                dbClub.name = club.name
            }
        }

        log.trace { "Updated club with id(${club.id.toHexString()})" }
    }

    override suspend fun delete(id: ObjectId) {
        log.debug { "Deleting club with id(${id.toHexString()})" }
        withContext(Dispatchers.IO) {
            realm.write {
                val club = queryById<Club>(id)

                if (club != null) {
                    require(query<Participant>("club._id == $0", club.id).count().find() == 0L) {
                        "Club with id ${club.id.toHexString()} is used by tournaments"
                    }

                    delete(club)
                    log.trace { "Club with id ${club.id.toHexString()} deleted" }
                } else {
                    log.warn { "Club with id ${id.toHexString()} doesn't exist" }
                }
            }
        }
    }

    override suspend fun getParticipantsWithClub(id: ObjectId): Int = withContext(Dispatchers.IO) {
        realm.query<Participant>("club._id == $0", id)
            .count()
            .asFlow()
            .first()
            .toInt()
    }

    companion object {
        private val log = logger()
    }
}
