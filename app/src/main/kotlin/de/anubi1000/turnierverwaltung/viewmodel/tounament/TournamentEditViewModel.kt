package de.anubi1000.turnierverwaltung.viewmodel.tounament

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.EditTournament
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.data.toEditTournament
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class TournamentEditViewModel(
    private val tournamentRepository: TournamentRepository
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)

    private var isEditMode = false

    fun loadCreate() {
        val tournament = EditTournament()
        state = State.Loaded(
            tournament = tournament,
            isValid = getValidationState(tournament)
        )
        isEditMode = false
    }

    fun loadEdit(id: ObjectId) {
        viewModelScope.launch {
            val tournament = tournamentRepository.getById(id)!!.toEditTournament()
            state = State.Loaded(
                tournament = tournament,
                isValid = getValidationState(tournament)
            )
            isEditMode = true
        }
    }

    fun saveChanges(onSaved: (ObjectId) -> Unit) {
        val currentState = state
        require(currentState is State.Loaded && currentState.isValid.value)

        viewModelScope.launch {
            val tournament = currentState.tournament.toTournament()
            if (!isEditMode) {
                tournamentRepository.insert(tournament)
            } else {
                tournamentRepository.update(tournament)
            }
            onSaved(tournament.id)
        }
    }

    private fun getValidationState(tournament: EditTournament): ComposeState<Boolean> = derivedStateOf {
        tournament.name.isNotEmpty()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val tournament: EditTournament, val isValid: ComposeState<Boolean>) : State
    }
}