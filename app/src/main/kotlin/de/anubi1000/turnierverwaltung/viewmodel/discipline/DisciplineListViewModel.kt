package de.anubi1000.turnierverwaltung.viewmodel.discipline

import de.anubi1000.turnierverwaltung.data.repository.DisciplineRepository
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId

@KoinViewModel
class DisciplineListViewModel(repository: DisciplineRepository, @InjectedParam private val tournamentId: ObjectId) : BaseListViewModel<Discipline, DisciplineRepository>(
    repository
) {
    override fun DisciplineRepository.getItemFlow(): Flow<List<Discipline>> {
        return getAllDisciplinesForTournamentAsFlow(tournamentId)
    }
}