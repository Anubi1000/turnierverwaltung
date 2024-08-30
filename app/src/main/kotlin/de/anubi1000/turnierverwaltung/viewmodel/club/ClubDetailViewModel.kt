package de.anubi1000.turnierverwaltung.viewmodel.club

import de.anubi1000.turnierverwaltung.data.repository.ClubRepository
import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseDetailViewModel
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class ClubDetailViewModel(repository: ClubRepository) : BaseDetailViewModel<Club, ClubRepository>(repository) {
    override suspend fun ClubRepository.getItemById(id: ObjectId): Club? = getClubById(id)

    override suspend fun ClubRepository.deleteItem(item: Club) {
        deleteClub(item.id)
    }
}