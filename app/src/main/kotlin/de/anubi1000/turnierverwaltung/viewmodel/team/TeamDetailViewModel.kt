package de.anubi1000.turnierverwaltung.viewmodel.team

import de.anubi1000.turnierverwaltung.data.repository.TeamRepository
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseDetailViewModel
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class TeamDetailViewModel(
    repository: TeamRepository,
) : BaseDetailViewModel<Team, TeamRepository>(repository) {
    override suspend fun TeamRepository.getItemById(id: ObjectId): Team? {
        return getTeamById(id)
    }

    override suspend fun TeamRepository.deleteItem(item: Team) {
        deleteTeamById(item.id)
    }
}