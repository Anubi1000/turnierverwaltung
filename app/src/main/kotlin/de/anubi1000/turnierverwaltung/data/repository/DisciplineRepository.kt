package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Discipline
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface DisciplineRepository {
    fun getAllDisciplinesForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Discipline>>
}