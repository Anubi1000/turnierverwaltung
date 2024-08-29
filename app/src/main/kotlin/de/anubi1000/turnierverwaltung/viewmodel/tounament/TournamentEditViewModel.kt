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
import org.apache.logging.log4j.kotlin.logger
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class TournamentEditViewModel(private val tournamentRepository: TournamentRepository) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    private var isEditMode = false

    fun loadCreate() {
        log.debug("Loading tournament for creation")
        isEditMode = false
        setLoaded(EditTournament(), null)
    }

    fun loadEdit(id: ObjectId) {
        log.debug { "Loading tournament for editing. id=${id.toHexString()}" }
        viewModelScope.launch {
            val tournament = tournamentRepository.getTournamentById(id)
            isEditMode = true
            setLoaded(tournament!!.toEditTournament(), tournament.name)
        }
    }

    private fun setLoaded(tournament: EditTournament, originalName: String?) {
        val isValid = derivedStateOf {
            tournament.name.isNotBlank()
        }
        state = State.Loaded(tournament, isValid, originalName)
    }

    fun saveChanges(onSaved: (ObjectId) -> Unit) {
        val state = state
        require(
            state is State.Loaded
        ) { "State needs to be loaded" }

        state.tournament.blockEditing()

        require(state.isValid.value) { "Tournament is not valid" }

        log.debug {
            if (isEditMode) {
                "Saving changes for tournament edit with id ${state.tournament.id.toHexString()}"
            } else {
                "Saving changes for tournament creation"
            }
        }

        if (isEditMode) {
            viewModelScope.launch {
                tournamentRepository.updateTournament(state.tournament)
                onSaved(state.tournament.id)
            }
        } else {
            viewModelScope.launch {
                tournamentRepository.insertTournament(state.tournament.toTournament())
                onSaved(state.tournament.id)
            }
        }
    }

    companion object {
        private val log = logger()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(
            val tournament: EditTournament,
            val isValid: ComposeState<Boolean>,
            val originalName: String?
        ) : State
    }
}