package de.anubi1000.turnierverwaltung.viewmodel.tounament

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.ScoreboardData
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.data.toScoreboardData
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentDetailViewModel.State
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class TournamentScoreboardViewModel(
    private val tournamentRepository: TournamentRepository,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)

    fun loadItem(id: ObjectId) {
        viewModelScope.launch {
            val tournament = tournamentRepository.getById(id)!!
            state = State.Loaded(tournament.toScoreboardData())
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val data: ScoreboardData) : State
    }
}
