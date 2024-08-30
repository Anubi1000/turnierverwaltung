package de.anubi1000.turnierverwaltung.viewmodel.club

import de.anubi1000.turnierverwaltung.data.repository.ClubRepository
import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId

@KoinViewModel
class ClubListViewModel(repository: ClubRepository, @InjectedParam private val tournamentId: ObjectId) : BaseListViewModel<Club, ClubRepository>(repository) {
    override fun ClubRepository.getItemFlow(): Flow<List<Club>> = getAllAsFlow(tournamentId)
}