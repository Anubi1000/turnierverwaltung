package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Team
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.apache.logging.log4j.kotlin.logger
import org.koin.core.annotation.Factory

@Factory
class TeamRepositoryImpl(private val realm: Realm) : TeamRepository {
    override fun getAllAsFlow(): Flow<List<Team>> {
        log.debug("Retrieving all teams as flow")
        return realm.query<Team>()
            .sort("name" to Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    companion object {
        private val log = logger()
    }
}