package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.database.queryById
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.kotlin.logger
import org.koin.core.annotation.Factory
import org.mongodb.kbson.ObjectId

interface TeamRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Team>>
    suspend fun getById(id: ObjectId): Team?
    suspend fun insert(team: Team, tournamentId: ObjectId)
    suspend fun update(team: Team)
    suspend fun delete(id: ObjectId)
}

@Factory
class TeamRepositoryImpl(private val realm: Realm) : TeamRepository {
    override fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Team>> {
        log.debug { "Retrieving all teams for tournament(${tournamentId.toHexString()})" }

        return realm.query<Team>("tournament._id == $0", tournamentId)
            .sort("name" to Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getById(id: ObjectId): Team? {
        log.debug { "Retrieving teams by id(${id.toHexString()})" }

        return withContext(Dispatchers.IO) {
            realm.queryById<Team>(id)
        }
    }

    override suspend fun insert(team: Team, tournamentId: ObjectId) {
        log.debug {
            "Inserting new team with id(${team.id.toHexString()}) for tournament(${tournamentId.toHexString()})"
        }

        withContext(Dispatchers.IO) {
            realm.write {
                val tournament = queryById<Tournament>(tournamentId)
                    ?: throw IllegalArgumentException("Tournament with specified id not found")

                require(team.members.size == tournament.teamSize) {
                    "Team needs to have the required sze"
                }

                val members = mapParticipants(team)
                val participatingDisciplines = mapTeamDisciplines(team)

                team.members = members
                team.participatingDisciplines = participatingDisciplines
                val dbTeam = copyToRealm(team)
                tournament.teams.add(dbTeam)
            }
        }

        log.trace { "Inserted team with id(${team.id.toHexString()})" }
    }

    override suspend fun update(team: Team) {
        log.debug { "Updating team with id(${team.id.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val dbTeam = queryById<Team>(team.id)
                    ?: throw IllegalArgumentException("Team with specified id not found")

                val members = mapParticipants(team)
                val participatingDisciplines = mapTeamDisciplines(team)

                dbTeam.name = team.name
                dbTeam.startNumber = team.startNumber
                dbTeam.members = members
                dbTeam.participatingDisciplines = participatingDisciplines
            }
        }

        log.trace { "Updated team with id(${team.id.toHexString()})" }
    }

    override suspend fun delete(id: ObjectId) {
        log.debug { "Deleting team with id(${id.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val team = queryById<Team>(id)

                if (team != null) {
                    delete(team)
                    log.trace { "Team with id(${team.id.toHexString()}) deleted" }
                } else {
                    log.warn { "Team with id ${id.toHexString()} doesn't exist" }
                }
            }
        }
    }

    private fun MutableRealm.mapParticipants(team: Team): RealmList<Participant> = team.members.map {
        val participant = findLatest(it)
        requireNotNull(participant) {
            "Participant with id ${it.id.toHexString()} not found"
        }
        participant
    }.toRealmList()

    private fun MutableRealm.mapTeamDisciplines(
        team: Team,
    ): RealmList<TeamDiscipline> = team.participatingDisciplines.map {
        val teamDiscipline = findLatest(it)
        requireNotNull(teamDiscipline) {
            "Team discipline with id ${it.id.toHexString()} not found"
        }
        teamDiscipline
    }.toRealmList()

    companion object {
        private val log = logger()
    }
}
