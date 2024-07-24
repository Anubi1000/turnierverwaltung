package de.anubi1000.turnierverwaltung.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.anubi1000.turnierverwaltung.data.participant.EditParticipant
import de.anubi1000.turnierverwaltung.data.participant.toEditParticipant
import de.anubi1000.turnierverwaltung.data.repository.ParticipantRepository
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import org.mongodb.kbson.ObjectId

class ParticipantEditViewModel(private val participantRepository: ParticipantRepository) : StateScreenModel<ParticipantEditViewModel.State>(State.Loading) {
    sealed interface State {
        data object Loading : State
        data class Loaded(val participant: EditParticipant) : State
    }

    private var tournamentId: ObjectId? = null
    var editMode by mutableStateOf(false)
        private set

    fun loadCreate(tournamentId: ObjectId) {
        log.debug("Loading participant creation")
        editMode = false
        this.tournamentId = tournamentId
        mutableState.value = State.Loaded(EditParticipant())
    }

    fun loadEdit(participantId: ObjectId) {
        log.debug { "Loading participant edit for id ${participantId.toHexString()}" }
        screenModelScope.launch {
            val participant = participantRepository.getParticipantById(participantId)
            editMode = true
            mutableState.value = State.Loaded(participant!!.toEditParticipant())
        }
    }

    companion object {
        private val log = logger()
    }
}