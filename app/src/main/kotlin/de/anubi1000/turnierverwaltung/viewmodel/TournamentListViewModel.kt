package de.anubi1000.turnierverwaltung.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.database.model.Tournament
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger

class TournamentListViewModel(private val tournamentRepository: TournamentRepository) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    fun loadTournaments() {
        if (state is State.Loaded) {
            log.debug("Skipping loading because tournaments are already loaded")
            return
        } else {
            log.debug("Loading all tournaments for list")
        }

        viewModelScope.launch {
            val flow = tournamentRepository
                .getAllAsFlow()
                .stateIn(scope = viewModelScope)
            state = State.Loaded(flow)
        }
    }

    companion object {
        private val log = logger()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val tournamentFlow: StateFlow<List<Tournament>>) : State
    }
}