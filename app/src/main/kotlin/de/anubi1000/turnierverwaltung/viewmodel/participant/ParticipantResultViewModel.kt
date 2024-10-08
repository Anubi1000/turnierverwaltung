package de.anubi1000.turnierverwaltung.viewmodel.participant

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.edit.EditParticipantResult
import de.anubi1000.turnierverwaltung.data.edit.toEditParticipantResult
import de.anubi1000.turnierverwaltung.data.repository.DisciplineRepository
import de.anubi1000.turnierverwaltung.data.repository.ParticipantRepository
import de.anubi1000.turnierverwaltung.data.validation.validateDouble
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class ParticipantResultViewModel(
    private val participantRepository: ParticipantRepository,
    private val disciplineRepository: DisciplineRepository,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)

    fun loadResults(participantId: ObjectId, disciplineId: ObjectId) {
        viewModelScope.launch {
            val participant = participantRepository.getById(participantId)!!
            val discipline = disciplineRepository.getById(disciplineId)!!
            val disciplineResult = participant.results[disciplineId.toHexString()] ?: Participant.DisciplineResult()
            val editParticipant = disciplineResult.toEditParticipantResult()
            state = State.Loaded(
                result = editParticipant,
                discipline = discipline,
                isValid = derivedStateOf {
                    editParticipant.rounds.all { round ->
                        round.values.all { validateDouble(it.value) != null }
                    }
                },
                participantId = participant.id,
            )
        }
    }

    fun saveChanges(onSaved: () -> Unit) {
        val currentState = state
        require(currentState is State.Loaded)

        viewModelScope.launch {
            val disciplineResult = currentState.result.toDisciplineResult()
            participantRepository.updateResult(
                participantId = currentState.participantId,
                disciplineId = currentState.discipline.id,
                result = disciplineResult,
            )
            onSaved()
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(
            val result: EditParticipantResult,
            val discipline: Discipline,
            val isValid: ComposeState<Boolean>,
            val participantId: ObjectId,
        ) : State
    }
}
