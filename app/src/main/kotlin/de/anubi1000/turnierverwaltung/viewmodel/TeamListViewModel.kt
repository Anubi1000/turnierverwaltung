package de.anubi1000.turnierverwaltung.viewmodel

import de.anubi1000.turnierverwaltung.data.repository.TeamRepository
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class TeamListViewModel(teamRepository: TeamRepository) : BaseListViewModel<Team, TeamRepository>(teamRepository) {
    override suspend fun TeamRepository.getItemFlow(): Flow<List<Team>> = getAllAsFlow()
}