package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Discipline
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import org.mongodb.kbson.ObjectId

@Factory
class DisciplineRepositoryImpl(private val realm: Realm) : DisciplineRepository {
    override fun getAllDisciplinesForTournamentAsFlow(tournamentId: ObjectId): Flow<List<Discipline>> {
        return realm.query<Discipline>("tournament._id == $0", tournamentId)
            .sort("name", Sort.ASCENDING)
            .asFlow()
            .map { it.list }
    }
}