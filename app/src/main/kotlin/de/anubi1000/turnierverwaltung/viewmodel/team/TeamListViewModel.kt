package de.anubi1000.turnierverwaltung.viewmodel.team

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.TeamRepository
import de.anubi1000.turnierverwaltung.database.model.Team
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId

@KoinViewModel
class TeamListViewModel(
    private val teamRepository: TeamRepository,
    @InjectedParam private val tournamentId: ObjectId
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)

    fun loadList() {
        if (state is State.Loaded) return

        viewModelScope.launch {
            val flow = teamRepository.getAllForTournamentAsFlow(tournamentId)
                .stateIn(scope = viewModelScope)
            state = State.Loaded(flow)
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val itemFlow: StateFlow<List<Team>>) : State
    }
}