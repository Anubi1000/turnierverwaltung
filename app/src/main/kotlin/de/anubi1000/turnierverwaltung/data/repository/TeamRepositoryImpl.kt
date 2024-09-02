package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.database.queryById
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.apache.logging.log4j.kotlin.logger
import org.koin.core.annotation.Factory
import org.mongodb.kbson.ObjectId

@Factory
class TeamRepositoryImpl(private val realm: Realm) : TeamRepository {
    override fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Team>> {
        log.debug("Retrieving all teams as flow")
        return realm.query<Team>("tournament._id == $0", tournamentId)
            .sort("name" to Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun updateTeam(team: Team) {
        realm.write {
            val dbTeam = queryById<Team>(team.id)!!
            dbTeam.name = team.name
            dbTeam.startNumber = team.startNumber
            dbTeam.members = team.members.map { findLatest(it)!! }.toRealmList()

        }
    }

    override suspend fun getTeamById(id: ObjectId): Team? {
        return realm.queryById<Team>(id)
    }

    override suspend fun deleteTeamById(id: ObjectId) {
        realm.write {
            delete(queryById<Team>(id)!!)
        }
    }

    override suspend fun insertTeam(team: Team, tournamentId: ObjectId) {
        realm.write {
            val tournament = queryById<Tournament>(tournamentId)!!
            team.members = team.members.map { findLatest(it)!! }.toRealmList()
            val dbTeam = copyToRealm(team)
            tournament.teams.add(dbTeam)
        }
    }

    companion object {
        private val log = logger()
    }
}