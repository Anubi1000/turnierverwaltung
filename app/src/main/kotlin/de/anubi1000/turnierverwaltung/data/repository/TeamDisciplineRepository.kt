package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Discipline
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

interface TeamDisciplineRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<TeamDiscipline>>
    suspend fun getById(id: ObjectId): TeamDiscipline?
    suspend fun insert(teamDiscipline: TeamDiscipline, tournamentId: ObjectId)
    suspend fun update(teamDiscipline: TeamDiscipline)
    suspend fun delete(id: ObjectId)
}

@Factory
class TeamDisciplineRepositoryImpl(private val realm: Realm) : TeamDisciplineRepository {
    override fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<TeamDiscipline>> {
        log.debug { "Retrieving all team disciplines for tournament(${tournamentId.toHexString()})" }

        return realm.query<TeamDiscipline>("tournament._id == $0", tournamentId)
            .sort("name", Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getById(id: ObjectId): TeamDiscipline? {
        log.debug { "Retrieving team discipline by id(${id.toHexString()})" }

        return withContext(Dispatchers.IO) {
            realm.queryById(id)
        }
    }

    override suspend fun insert(teamDiscipline: TeamDiscipline, tournamentId: ObjectId) {
        log.debug { "Inserting new team discipline with id(${teamDiscipline.id.toHexString()}) for tournament(${tournamentId.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val tournament = queryById<Tournament>(tournamentId) ?: throw IllegalArgumentException("Tournament with specified id not found")

                val basedOn = mapDisciplines(teamDiscipline)

                teamDiscipline.basedOn = basedOn
                val dbTeamDiscipline = copyToRealm(teamDiscipline)
                tournament.teamDisciplines.add(dbTeamDiscipline)
            }
        }

        log.trace { "Inserted team discipline with id(${teamDiscipline.id.toHexString()})" }
    }

    override suspend fun update(teamDiscipline: TeamDiscipline) {
        log.debug { "Updating team discipline with id(${teamDiscipline.id.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val dbTeamDiscipline = queryById<TeamDiscipline>(teamDiscipline.id) ?: throw IllegalArgumentException("Team discipline with specified id not found")

                val basedOn = mapDisciplines(teamDiscipline)

                dbTeamDiscipline.name = teamDiscipline.name
                dbTeamDiscipline.basedOn = basedOn
            }
        }

        log.trace { "Updated team discipline with id(${teamDiscipline.id.toHexString()})" }
    }

    override suspend fun delete(id: ObjectId) {
        log.debug { "Deleting team discipline with id(${id.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val teamDiscipline = queryById<TeamDiscipline>(id)

                if (teamDiscipline != null) {
                    val teams = teamDiscipline.tournament.first().teams

                    teams.forEach { team ->
                        team.participatingDisciplines.remove(teamDiscipline)
                    }

                    delete(teamDiscipline)
                    log.trace { "Team discipline with id(${teamDiscipline.id.toHexString()}) deleted" }
                } else {
                    log.warn { "Team discipline with id ${id.toHexString()} doesn't exist" }
                }
            }
        }
    }

    private fun MutableRealm.mapDisciplines(teamDiscipline: TeamDiscipline): RealmList<Discipline> {
        val basedOn = teamDiscipline.basedOn.map {
            val discipline = findLatest(it)
            requireNotNull(discipline) {
                "Discipline with id ${it.id.toHexString()} not found"
            }
            discipline
        }.toRealmList()
        return basedOn
    }

    companion object {
        private val log = logger()
    }
}
