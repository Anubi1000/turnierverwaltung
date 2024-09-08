package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.database.queryById
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
    override fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<TeamDiscipline>> = realm.query<TeamDiscipline>("tournament._id == $0", tournamentId)
        .sort("name", Sort.ASCENDING)
        .asFlow()
        .map { it.list }

    override suspend fun getById(id: ObjectId): TeamDiscipline? = withContext(Dispatchers.IO) {
        realm.queryById(id)
    }

    override suspend fun insert(teamDiscipline: TeamDiscipline, tournamentId: ObjectId) {
        withContext(Dispatchers.IO) {
            realm.write {
                val tournament = queryById<Tournament>(tournamentId)!!

                teamDiscipline.basedOn = teamDiscipline.basedOn.map { findLatest(it)!! }.toRealmList()
                val dbTeamDiscipline = copyToRealm(teamDiscipline)
                tournament.teamDisciplines.add(dbTeamDiscipline)
            }
        }
    }

    override suspend fun update(teamDiscipline: TeamDiscipline) {
        withContext(Dispatchers.IO) {
            realm.write {
                val dbTeamDiscipline = queryById<TeamDiscipline>(teamDiscipline.id)!!

                dbTeamDiscipline.name = teamDiscipline.name
                dbTeamDiscipline.basedOn = teamDiscipline.basedOn.map { findLatest(it)!! }.toRealmList()
            }
        }
    }

    override suspend fun delete(id: ObjectId) {
        withContext(Dispatchers.IO) {
            realm.write {
                val teamDiscipline = queryById<TeamDiscipline>(id)!!
                delete(teamDiscipline)
            }
        }
    }
}
