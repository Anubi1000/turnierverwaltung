package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Club
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.apache.logging.log4j.kotlin.logger
import org.koin.core.annotation.Factory

@Factory
class ClubRepositoryImpl(private val realm: Realm) : ClubRepository {
    override fun getAllAsFlow(): Flow<List<Club>> {
        log.debug("Retrieving all clubs as flow")
        return realm.query<Club>()
            .sort("name", Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    companion object {
        private val log = logger()
    }
}