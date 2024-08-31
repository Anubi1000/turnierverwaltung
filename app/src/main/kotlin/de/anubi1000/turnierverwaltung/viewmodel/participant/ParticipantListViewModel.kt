package de.anubi1000.turnierverwaltung.viewmodel.participant

import de.anubi1000.turnierverwaltung.data.repository.ParticipantRepository
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseListViewModel
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId

@KoinViewModel
class ParticipantListViewModel(participantRepository: ParticipantRepository, @InjectedParam private val tournamentId: ObjectId) : BaseListViewModel<Participant, ParticipantRepository>(participantRepository) {
    override fun ParticipantRepository.getItemFlow(): Flow<List<Participant>> = getAllForTournamentAsFlow(tournamentId)
}