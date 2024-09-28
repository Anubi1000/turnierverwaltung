package de.anubi1000.turnierverwaltung.viewmodel.club

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.ClubRepository
import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.util.performSearch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId

@KoinViewModel
class ClubListViewModel(
    private val clubRepository: ClubRepository,
    @InjectedParam private val tournamentId: ObjectId,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)

    fun loadList() {
        if (state is State.Loaded) return

        viewModelScope.launch {
            val clubFlow = clubRepository.getAllForTournamentAsFlow(tournamentId)
            val searchValueFlow = MutableStateFlow("")

            val flow = combine(clubFlow, searchValueFlow) { clubs, searchValue ->
                clubs.performSearch(searchValue) { it.name }
            }.stateIn(scope = viewModelScope)
            state = State.Loaded(flow, searchValueFlow)
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val itemFlow: StateFlow<List<Club>>, val searchValueFlow: MutableStateFlow<String>) : State
    }
}
