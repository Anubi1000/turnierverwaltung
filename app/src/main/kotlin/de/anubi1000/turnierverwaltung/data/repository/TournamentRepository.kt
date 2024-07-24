package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.data.tournament.ListTournament
import de.anubi1000.turnierverwaltung.data.tournament.Tournament
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface TournamentRepository {
    fun getAllAsFlow(): Flow<ImmutableList<ListTournament>>
    suspend fun getTournamentById(id: ObjectId): Tournament?
    suspend fun insertTournament(tournament: Tournament)
    suspend fun updateTournament(tournament: Tournament)
    suspend fun deleteTournament(id: ObjectId)
}