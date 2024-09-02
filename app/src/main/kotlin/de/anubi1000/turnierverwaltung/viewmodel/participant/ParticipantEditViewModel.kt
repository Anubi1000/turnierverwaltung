package de.anubi1000.turnierverwaltung.viewmodel.participant

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.EditParticipant
import de.anubi1000.turnierverwaltung.data.repository.ClubRepository
import de.anubi1000.turnierverwaltung.data.repository.ParticipantRepository
import de.anubi1000.turnierverwaltung.data.toEditParticipant
import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.database.model.Participant
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class ParticipantEditViewModel(
    private val participantRepository: ParticipantRepository,
    private val clubRepository: ClubRepository,
    @InjectedParam private val tournamentId: ObjectId
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    private var isEditMode = false

    fun loadCreate() {
        val participant = EditParticipant()

        viewModelScope.launch {
            isEditMode = false

            state = State.Loaded(
                participant = participant,
                clubs = clubRepository.getAllForTournament(tournamentId),
                isValid = getValidationState(participant)
            )
        }
    }

    fun loadEdit(participantId: ObjectId) {
        viewModelScope.launch {
            isEditMode = true

            val participant = participantRepository.getParticipantById(participantId)!!.toEditParticipant()
            state = State.Loaded(
                participant = participant,
                clubs = clubRepository.getAllForTournament(tournamentId),
                isValid = getValidationState(participant)
            )
        }
    }

    private fun getValidationState(participant: EditParticipant): ComposeState<Boolean> {
        return derivedStateOf {
            participant.name.isNotBlank() &&
                    participant.clubId != null
        }
    }

    fun saveChanges(onSaved: (ObjectId) -> Unit) {
        val currentState = state

        require(currentState is State.Loaded)
        require(currentState.isValid.value)

        viewModelScope.launch {
            val participant = Participant().also {
                it.id = currentState.participant.id
                it.name = currentState.participant.name
                it.startNumber = currentState.participant.startNumber
                it.gender = currentState.participant.gender

                it.club = clubRepository.getClubById(currentState.participant.clubId!!)
            }

            if (isEditMode) {
                participantRepository.updateParticipant(participant)
            } else {
                participantRepository.insertParticipant(
                    participant = participant,
                    tournamentId = tournamentId
                )
            }
            onSaved(participant.id)
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(
            val participant: EditParticipant,
            val clubs: List<Club>,
            val isValid: ComposeState<Boolean>
        ) : State
    }
}