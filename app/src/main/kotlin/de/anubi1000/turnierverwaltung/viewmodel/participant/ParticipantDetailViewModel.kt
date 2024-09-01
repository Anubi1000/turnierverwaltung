package de.anubi1000.turnierverwaltung.viewmodel.participant

import androidx.lifecycle.ViewModel
import de.anubi1000.turnierverwaltung.data.repository.ParticipantRepository
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseDetailViewModel
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class ParticipantDetailViewModel(
    repository: ParticipantRepository
) : BaseDetailViewModel<Participant, ParticipantRepository>(repository) {
    override suspend fun ParticipantRepository.getItemById(id: ObjectId): Participant? {
        return getParticipantById(id)
    }

    override suspend fun ParticipantRepository.deleteItem(item: Participant) {
        deleteParticipant(item.id)
    }
}