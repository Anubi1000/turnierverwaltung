package de.anubi1000.turnierverwaltung.viewmodel.team

import de.anubi1000.turnierverwaltung.data.repository.TeamRepository
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId

@KoinViewModel
class TeamListViewModel(teamRepository: TeamRepository, @InjectedParam private val tournamentId: ObjectId) : BaseListViewModel<Team, TeamRepository>(teamRepository) {
    override fun TeamRepository.getItemFlow(): Flow<List<Team>> = getAllForTournamentAsFlow(tournamentId)
}