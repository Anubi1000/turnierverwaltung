package de.anubi1000.turnierverwaltung.viewmodel.participant

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.ParticipantRepository
import de.anubi1000.turnierverwaltung.database.model.Participant
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class ParticipantDetailViewModel(
    private val participantRepository: ParticipantRepository
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    fun loadItem(id: ObjectId) {
        viewModelScope.launch {
            val participant = participantRepository.getById(id)!!
            state = State.Loaded(participant)
        }
    }

    fun deleteItem(onDeleted: () -> Unit) {
        val currentState = state
        require(currentState is State.Loaded)

        viewModelScope.launch {
            participantRepository.delete(currentState.item.id)
            onDeleted()
        }
    }


    sealed interface State {
        data object Loading : State
        data class Loaded(val item: Participant) : State
    }
}