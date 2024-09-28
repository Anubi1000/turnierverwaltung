package de.anubi1000.turnierverwaltung.viewmodel.tounament

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.util.performSearch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class TournamentListViewModel(
    private val tournamentRepository: TournamentRepository,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)

    fun loadList() {
        if (state is State.Loaded) return

        viewModelScope.launch {
            val tournamentFlow = tournamentRepository.getAllAsFlow()
            val searchValueFlow = MutableStateFlow("")

            val flow = combine(tournamentFlow, searchValueFlow) { tournaments, searchValue ->
                tournaments.performSearch(searchValue) { it.name }
            }.stateIn(scope = viewModelScope)
            state = State.Loaded(flow, searchValueFlow)
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val itemFlow: StateFlow<List<Tournament>>, val searchValueFlow: MutableStateFlow<String>) : State
    }
}
