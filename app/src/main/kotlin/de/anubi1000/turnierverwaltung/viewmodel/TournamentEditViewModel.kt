package de.anubi1000.turnierverwaltung.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.data.tournament.EditTournament
import de.anubi1000.turnierverwaltung.data.tournament.Tournament
import de.anubi1000.turnierverwaltung.data.tournament.toEditTournament
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

class TournamentEditViewModel(private val tournamentRepository: TournamentRepository) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    private var originalTournament: Tournament? = null
    private var isEditMode = false

    fun loadCreate() {
        log.debug("Loading tournament creation")
        originalTournament = null
        isEditMode = false
        setLoaded(EditTournament())
    }

    fun loadEdit(id: ObjectId) {
        log.debug { "Loading tournament edit for id ${id.toHexString()}" }
        viewModelScope.launch {
            val tournament = tournamentRepository.getTournamentById(id)
            originalTournament = tournament
            isEditMode = true
            setLoaded(tournament!!.toEditTournament())
        }
    }

    private fun setLoaded(tournament: EditTournament) {
        val isValid = derivedStateOf {
            tournament.name.isNotBlank() &&
                    tournament.values.isNotEmpty() &&
                    tournament.values.all { it.name.isNotBlank() }
        }
        state = State.Loaded(tournament, isValid)
    }

    fun saveChanges(onSaved: (ObjectId) -> Unit) {
        log.debug {
            if (isEditMode) {
                "Saving changes for tournament edit with id ${originalTournament?.id?.toHexString()}"
            } else {
                "Saving changes for tournament creation"
            }
        }

        val state = state
        require(
            state is State.Loaded
        ) { "State needs to be loaded" }

        require(state.isValid.value) { "Tournament is not valid" }

        val tournament = state.tournament.toTournament()

        if (isEditMode) {
            viewModelScope.launch {
                tournamentRepository.updateTournament(tournament)
                onSaved(tournament.id)
            }
        } else {
            viewModelScope.launch {
                tournamentRepository.insertTournament(tournament)
                onSaved(tournament.id)
            }
        }
    }

    companion object {
        private val log = logger()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val tournament: EditTournament, val isValid: ComposeState<Boolean>) : State
    }
}