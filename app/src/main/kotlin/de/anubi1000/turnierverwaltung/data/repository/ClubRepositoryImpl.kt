package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.queryById
import de.anubi1000.turnierverwaltung.database.model.Club
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
class ClubRepositoryImpl(private val realm: Realm) : ClubRepository {
    override fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Club>> {
        log.debug("Retrieving all clubs as flow")
        return realm.query<Club>("tournament._id == $0", tournamentId)
            .sort("name", Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getAllForTournament(tournamentId: ObjectId): List<Club> {
        log.debug("Retrieving all clubs as flow")
        return realm.query<Club>("tournament._id == $0", tournamentId)
            .sort("name", Sort.ASCENDING)
            .find()
    }

    override suspend fun getClubById(id: ObjectId): Club? {
        log.debug { "Querying club by id(${id.toHexString()})" }
        return realm.queryById(id)
    }

    override suspend fun insertClub(club: Club, tournamentId: ObjectId) {
        log.debug { "Inserting new club with id(${club.id.toHexString()})" }
        realm.write {
            val insertedClub = copyToRealm(club)
            queryById<Tournament>(tournamentId)!!.clubs.add(insertedClub)
        }
    }

    override suspend fun updateClub(club: Club) {
        log.debug { "Updating club with id(${club.id.toHexString()})" }
        realm.write {
            val databaseClub = queryById<Club>(club.id)!!

            databaseClub.name = club.name
        }
    }

    override suspend fun deleteClub(id: ObjectId) {
        log.debug { "Deleting club with id(${id.toHexString()})" }
        realm.write {
            delete(queryById<Club>(id)!!)
        }
    }

    companion object {
        private val log = logger()
    }
}