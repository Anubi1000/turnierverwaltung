package de.anubi1000.turnierverwaltung.viewmodel.discipline

import de.anubi1000.turnierverwaltung.data.repository.ClubRepository
import de.anubi1000.turnierverwaltung.data.repository.DisciplineRepository
import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseDetailViewModel
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class DisciplineDetailViewModel(repository: DisciplineRepository) : BaseDetailViewModel<Discipline, DisciplineRepository>(
    repository
) {
    override suspend fun DisciplineRepository.getItemById(id: ObjectId): Discipline? {
        return getDisciplineById(id)
    }

    override suspend fun DisciplineRepository.deleteItem(item: Discipline) {
        deleteDisciplineById(item.id)
    }
}