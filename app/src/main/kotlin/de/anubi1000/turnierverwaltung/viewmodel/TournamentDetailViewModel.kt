package de.anubi1000.turnierverwaltung.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.data.tournament.Tournament
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import org.mongodb.kbson.ObjectId

class TournamentDetailViewModel(private val tournamentRepository: TournamentRepository) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    fun loadTournament(id: ObjectId) {
        viewModelScope.launch {
            val tournament = tournamentRepository.getTournamentById(id)
            state = State.Loaded(tournament!!)
        }
    }

    fun deleteTournament() {
        val state = state
        require(state is State.Loaded) { "Tournament needs to be loaded" }
        log.debug("Deleting tournament ${state.tournament.id.toHexString()}")
        viewModelScope.launch {
            tournamentRepository.deleteTournament(state.tournament.id)
        }
    }

    companion object {
        private val log = logger()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val tournament: Tournament) : State
    }
}