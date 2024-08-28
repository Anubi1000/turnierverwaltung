package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Participant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.apache.logging.log4j.kotlin.logger

class ParticipantRepositoryImpl(private val realm: Realm) : ParticipantRepository {
    override fun getAllAsFlow(): Flow<List<Participant>> {
        log.debug("Retrieving all participants as flow")
        return realm.query<Participant>()
            .sort("name", Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    companion object {
        private val log = logger()
    }
}