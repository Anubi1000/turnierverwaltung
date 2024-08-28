package de.anubi1000.turnierverwaltung.viewmodel.tounament

import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel
import kotlinx.coroutines.flow.Flow

class TournamentListViewModel(tournamentRepository: TournamentRepository) : BaseListViewModel<Tournament, TournamentRepository>(tournamentRepository) {
    override suspend fun getFlowFromRepository(repository: TournamentRepository): Flow<List<Tournament>> {
        return repository.getAllAsFlow()
    }
}