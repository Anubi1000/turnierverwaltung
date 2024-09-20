package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.database.queryById
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.kotlin.logger
import org.koin.core.annotation.Factory
import org.mongodb.kbson.ObjectId

interface DisciplineRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Discipline>>
    suspend fun getById(id: ObjectId): Discipline?
    suspend fun insert(discipline: Discipline, tournamentId: ObjectId)
    suspend fun update(discipline: Discipline)
    suspend fun delete(id: ObjectId)
}

@Factory
class DisciplineRepositoryImpl(private val realm: Realm) : DisciplineRepository {
    override fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Discipline>> {
        log.debug { "Retrieving all disciplines for tournament(${tournamentId.toHexString()}) as flow" }

        return realm.query<Discipline>("tournament._id == $0", tournamentId)
            .sort("name", Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }

    override suspend fun getById(id: ObjectId): Discipline? {
        log.debug { "Retrieving discipline with id(${id.toHexString()})" }

        return withContext(Dispatchers.IO) {
            realm.queryById<Discipline>(id)
        }
    }

    override suspend fun insert(discipline: Discipline, tournamentId: ObjectId) {
        log.debug { "Inserting new discipline with id(${tournamentId.toHexString()}) for tournament(${tournamentId.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val tournament = queryById<Tournament>(tournamentId)
                require(tournament != null) {
                    "Tournament with id(${tournamentId.toHexString()}) doesn't exist"
                }

                val dbDiscipline = copyToRealm(discipline)
                tournament.disciplines.add(dbDiscipline)
            }
        }

        log.trace { "Inserted discipline with id(${discipline.id}" }
    }

    override suspend fun update(discipline: Discipline) {
        log.debug { "Updating discipline with id(${discipline.id})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val dbDiscipline = queryById<Discipline>(discipline.id)
                require(dbDiscipline != null) {
                    "Discipline with id(${discipline.id}) doesn't exist"
                }

                dbDiscipline.name = discipline.name
                dbDiscipline.isGenderSeparated = discipline.isGenderSeparated
                dbDiscipline.amountOfBestRoundsToShow = discipline.amountOfBestRoundsToShow

                require(dbDiscipline.values.size == discipline.values.size) {
                    "Amount of values for discipline can't differ"
                }

                require(dbDiscipline.values.distinct().size == dbDiscipline.values.distinct().size) {
                    "New values must be the same as old values"
                }

                dbDiscipline.values = discipline.values
            }
        }

        log.trace { "Updated discipline with id(${discipline.id})" }
    }

    override suspend fun delete(id: ObjectId) {
        log.debug { "Deleting discipline with id(${id.toHexString()})" }

        withContext(Dispatchers.IO) {
            realm.write {
                val discipline = queryById<Discipline>(id)

                if (discipline != null) {
                    val participants = discipline.tournament.first().participants

                    participants.forEach { participant ->
                        participant.results.remove(discipline.id.toHexString())
                    }

                    delete(discipline)
                    log.trace { "Discipline with id(${discipline.id.toHexString()}) deleted" }
                } else {
                    log.warn { "Discipline with id(${id.toHexString()}) doesn't exist" }
                }
            }
        }
    }

    companion object {
        private val log = logger()
    }
}
