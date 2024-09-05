package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.database.queryById
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory
import org.mongodb.kbson.ObjectId

@Factory
class DisciplineRepositoryImpl(private val realm: Realm) : DisciplineRepository {
    override fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Discipline>> {
        return realm.query<Discipline>("tournament._id == $0", tournamentId)
            .sort("name", Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getById(id: ObjectId): Discipline? {
        return withContext(Dispatchers.IO) {
            realm.queryById<Discipline>(id)
        }
    }

    override suspend fun delete(id: ObjectId) {
        withContext(Dispatchers.IO) {
            realm.write {
                delete(queryById<Discipline>(id)!!)
            }
        }
    }

    override suspend fun insert(discipline: Discipline, tournamentId: ObjectId) {
        withContext(Dispatchers.IO) {
            realm.write {
                val tournament = queryById<Tournament>(tournamentId)!!
                val dbDiscipline = copyToRealm(discipline)
                tournament.disciplines.add(dbDiscipline)
            }
        }
    }

    override suspend fun update(discipline: Discipline) {
        withContext(Dispatchers.IO) {
            realm.write {
                val dbDiscipline = queryById<Discipline>(discipline.id)!!
                dbDiscipline.name = discipline.name
                dbDiscipline.isGenderSeparated = discipline.isGenderSeparated
                dbDiscipline.values = discipline.values
            }
        }
    }
}