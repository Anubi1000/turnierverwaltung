package de.anubi1000.turnierverwaltung.viewmodel.tounament

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.server.Server
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class TournamentDetailViewModel(
    private val tournamentRepository: TournamentRepository,
    private val server: Server,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    fun loadItem(id: ObjectId) {
        viewModelScope.launch {
            val tournament = tournamentRepository.getById(id)!!
            state = State.Loaded(tournament)
        }
    }

    fun deleteItem(onDeleted: () -> Unit) {
        val currentState = state
        require(currentState is State.Loaded)

        viewModelScope.launch {
            tournamentRepository.delete(currentState.item.id)
            onDeleted()
        }
    }

    fun showTournamentOnScoreboard() {
        val currentState = state
        require(currentState is State.Loaded)

        log.debug("Changing tournament for scoreboard to current one")
        server.setCurrentTournament(currentState.item.id)
    }

    companion object {
        private val log = logger()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val item: Tournament) : State
    }
}
