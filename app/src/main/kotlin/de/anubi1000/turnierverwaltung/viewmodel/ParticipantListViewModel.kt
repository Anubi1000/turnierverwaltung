package de.anubi1000.turnierverwaltung.viewmodel

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.anubi1000.turnierverwaltung.data.participant.ListParticipant
import de.anubi1000.turnierverwaltung.data.repository.ParticipantRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import org.mongodb.kbson.ObjectId

class ParticipantListViewModel(private val participantRepository: ParticipantRepository) : StateScreenModel<ParticipantListViewModel.State>(State.Loading) {
    sealed interface State {
        data object Loading : State
        data class Loaded(val participantFlow: StateFlow<ImmutableList<ListParticipant>>) : State
    }

    fun loadParticipants(tournamentId: ObjectId) {
        log.debug { "Loading participants for list. TournamentId: ${tournamentId.toHexString()}" }
        if (mutableState.value is State.Loaded) {
            log.debug("Skipping because list is already loaded")
            return
        }

        val flow = participantRepository
            .getAllAsFlow(tournamentId)
            .stateIn(
                scope = screenModelScope,
                started = SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = 5000L
                ),
                initialValue = persistentListOf()
            )
        mutableState.value = State.Loaded(flow)
    }

    fun deleteParticipant(id: ObjectId) {
        log.debug { "Deleting participant ${id.toHexString()}" }
        screenModelScope.launch {
            participantRepository.deleteParticipant(id)
        }
    }

    companion object {
        private val log = logger()
    }
}