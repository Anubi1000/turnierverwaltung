package de.anubi1000.turnierverwaltung.viewmodel

import de.anubi1000.turnierverwaltung.data.repository.ParticipantRepository
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel
import kotlinx.coroutines.flow.Flow

class ParticipantListViewModel(participantRepository: ParticipantRepository) : BaseListViewModel<Participant, ParticipantRepository>(participantRepository) {
    override suspend fun getFlowFromRepository(repository: ParticipantRepository): Flow<List<Participant>> {
        return repository.getAllAsFlow()
    }
}