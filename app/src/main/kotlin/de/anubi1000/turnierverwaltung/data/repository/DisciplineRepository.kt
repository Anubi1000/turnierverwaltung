package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Discipline
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface DisciplineRepository {
    fun getAllForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Discipline>>
    suspend fun getById(id: ObjectId): Discipline?
    suspend fun insert(discipline: Discipline, tournamentId: ObjectId)
    suspend fun update(discipline: Discipline)
    suspend fun delete(id: ObjectId)
}