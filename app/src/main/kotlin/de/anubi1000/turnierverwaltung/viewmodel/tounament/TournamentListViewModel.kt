package de.anubi1000.turnierverwaltung.viewmodel.tounament

import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class TournamentListViewModel(repository: TournamentRepository) : BaseListViewModel<Tournament, TournamentRepository>(repository) {
    override fun TournamentRepository.getItemFlow(): Flow<List<Tournament>> = getAllAsFlow()
}