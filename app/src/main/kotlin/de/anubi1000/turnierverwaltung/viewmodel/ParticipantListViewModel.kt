package de.anubi1000.turnierverwaltung.viewmodel

import de.anubi1000.turnierverwaltung.data.repository.ParticipantRepository
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ParticipantListViewModel(participantRepository: ParticipantRepository) : BaseListViewModel<Participant, ParticipantRepository>(participantRepository) {
    override fun ParticipantRepository.getItemFlow(): Flow<List<Participant>> = getAllAsFlow()
}