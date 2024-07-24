package de.anubi1000.turnierverwaltung.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.data.tournament.ListTournament
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import org.mongodb.kbson.ObjectId

class TournamentListViewModel(private val tournamentRepository: TournamentRepository) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    fun loadTournaments() {
        log.debug("Loading all tournaments for list")
        if (state is State.Loaded) {
            log.debug("Skipping because list is already loaded")
            return
        }

        val flow = tournamentRepository.getAllAsFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = 5000L
                ),
                initialValue = persistentListOf()
            )
        state = State.Loaded(flow)
    }

    fun deleteTournament(id: ObjectId) {
        log.debug("Deleting tournament ${id.toHexString()}")
        viewModelScope.launch {
            tournamentRepository.deleteTournament(id)
        }
    }

    companion object {
        private val log = logger()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val tournamentFlow: StateFlow<ImmutableList<ListTournament>>) : State
    }
}