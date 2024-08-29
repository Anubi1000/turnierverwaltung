package de.anubi1000.turnierverwaltung.viewmodel

import de.anubi1000.turnierverwaltung.data.repository.ClubRepository
import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ClubListViewModel(clubRepository: ClubRepository) : BaseListViewModel<Club, ClubRepository>(clubRepository) {
    override suspend fun ClubRepository.getItemFlow(): Flow<List<Club>> = getAllAsFlow()
}