package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.data.tournament.ListTournament
import de.anubi1000.turnierverwaltung.data.tournament.Tournament
import de.anubi1000.turnierverwaltung.data.tournament.toListTournament
import de.anubi1000.turnierverwaltung.data.tournament.toTournament
import de.anubi1000.turnierverwaltung.database.model.ParticipantModel
import de.anubi1000.turnierverwaltung.database.model.TournamentModel
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.apache.logging.log4j.kotlin.logger
import org.mongodb.kbson.ObjectId

class TournamentRepositoryImpl(private val realm: Realm) : TournamentRepository {
    override fun getAllAsFlow(): Flow<ImmutableList<ListTournament>> {
        log.debug("Getting tournaments as flow")
        return realm.query<TournamentModel>()
            .sort("date" to Sort.DESCENDING, "name" to Sort.ASCENDING)
            .asFlow()
            .map { resultsChange ->
                resultsChange.list.map(TournamentModel::toListTournament).toImmutableList()
            }
    }

    override suspend fun getTournamentById(id: ObjectId): Tournament? {
        log.debug { "Getting tournament with id ${id.toHexString()}" }
        return realm
            .query<TournamentModel>("_id == $0", id)
            .asFlow()
            .first()
            .list
            .firstOrNull()
            ?.toTournament()
    }

    override suspend fun insertTournament(tournament: Tournament) {
        log.debug("Inserting new tournament")
        realm.write {
            val model = tournament.toTournamentModel()
            copyToRealm(model)
        }
    }

    override suspend fun updateTournament(tournament: Tournament) {
        log.debug { "Updating tournament with id ${tournament.id.toHexString()}" }
        realm.write {
            val liveTournament = query<TournamentModel>("_id == $0", tournament.id)
                .find()
                .first()

            liveTournament.name = tournament.name
            liveTournament.date = tournament.date
            tournament.values.forEach { value ->
                val liveValue = liveTournament.values.find {
                    it.id == value.id
                }!!
                liveValue.name = value.name
                liveValue.subtract = value.subtract
            }
        }
    }

    override suspend fun deleteTournament(id: ObjectId) {
        log.debug { "Deleting tournament with id ${id.toHexString()}" }
        realm.write {
            query<ParticipantModel>("tournament._id == $0", id)
                .find()
                .forEach(::delete)

            query<TournamentModel>("_id == $0", id)
                .find()
                .first()
                .also(::delete)
        }
    }

    companion object {
        private val log = logger()
    }
}