package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.getById
import de.anubi1000.turnierverwaltung.database.model.Club
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
    override fun getAllAsFlow(): Flow<List<Club>> {
        log.debug("Retrieving all clubs as flow")
        return realm.query<Club>()
            .sort("name", Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getClubById(id: ObjectId): Club? {
        log.debug { "Querying club by id(${id.toHexString()})" }
        return realm.getById(id)
    }

    override suspend fun insertClub(club: Club) {
        log.debug { "Inserting new club with id(${club.id.toHexString()})" }
        realm.write {
            copyToRealm(club)
        }
    }

    override suspend fun updateClub(club: Club) {
        log.debug { "Updating  club with id(${club.id.toHexString()})" }
        realm.write {
            val databaseClub = getById<Club>(club.id)!!

            databaseClub.name = club.name
        }
    }

    companion object {
        private val log = logger()
    }
}