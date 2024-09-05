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
        viewModelScope.launch {
            val participant = EditParticipant()
            state = State.Loaded(
                item = participant,
                clubs = clubRepository.getAllForTournament(tournamentId),
                isValid = getValidationState(participant)
            )
            isEditMode = false
        }
    }

    fun loadEdit(id: ObjectId) {
        viewModelScope.launch {
            val participant = participantRepository.getById(id)!!.toEditParticipant()
            state = State.Loaded(
                item = participant,
                clubs = clubRepository.getAllForTournament(tournamentId),
                isValid = getValidationState(participant)
            )
            isEditMode = true
        }
    }

    fun saveChanges(onSaved: (ObjectId) -> Unit) {
        val currentState = state
        require(currentState is State.Loaded && currentState.isValid.value)

        viewModelScope.launch {
            val participant = Participant().also {
                it.id = currentState.item.id
                it.name = currentState.item.name
                it.startNumber = currentState.item.startNumber
                it.gender = currentState.item.gender

                it.club = currentState.clubs.find { it.id == currentState.item.clubId }!!
            }

            if (!isEditMode) {
                participantRepository.insert(participant, tournamentId)
            } else {
                participantRepository.update(participant)
            }
            onSaved(participant.id)
        }
    }

    private fun getValidationState(participant: EditParticipant): ComposeState<Boolean> = derivedStateOf {
        participant.name.isNotBlank() &&
                participant.clubId != null
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(
            val item: EditParticipant,
            val clubs: List<Club>,
            val isValid: ComposeState<Boolean>
        ) : State
    }
}