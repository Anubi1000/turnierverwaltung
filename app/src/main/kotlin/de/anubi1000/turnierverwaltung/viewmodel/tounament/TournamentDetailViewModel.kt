package de.anubi1000.turnierverwaltung.viewmodel.tounament

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.server.ServerViewModel
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class TournamentDetailViewModel(
    private val tournamentRepository: TournamentRepository,
    private val serverViewModel: ServerViewModel
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    fun loadTournament(id: ObjectId) {
        log.debug { "Loading tournament for detail screen. id=${id.toHexString()}" }
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

    fun showTournamentOnScoreboard() {
        val state = state
        require(state is State.Loaded) { "Tournament needs to be loaded" }

        log.debug("Changing tournament for scoreboard to current one")
        serverViewModel.setCurrentTournament(state.tournament)
    }

    companion object {
        private val log = logger()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val tournament: Tournament) : State
    }
}