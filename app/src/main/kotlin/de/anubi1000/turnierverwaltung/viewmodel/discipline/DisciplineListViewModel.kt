package de.anubi1000.turnierverwaltung.viewmodel.discipline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.DisciplineRepository
import de.anubi1000.turnierverwaltung.data.repository.TeamDisciplineRepository
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId

@KoinViewModel
class DisciplineListViewModel(
    private val disciplineRepository: DisciplineRepository,
    private val teamDisciplineRepository: TeamDisciplineRepository,
    @InjectedParam private val tournamentId: ObjectId,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)

    fun loadList() {
        if (state is State.Loaded) return

        viewModelScope.launch {
            val disciplineFlow = disciplineRepository.getAllForTournamentAsFlow(tournamentId)
            val teamDisciplineFlow = teamDisciplineRepository.getAllForTournamentAsFlow(tournamentId)
            state = State.Loaded(
                disciplineFlow = disciplineFlow.stateIn(viewModelScope),
                teamDisciplineFlow = teamDisciplineFlow.stateIn(viewModelScope),
            )
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val disciplineFlow: StateFlow<List<Discipline>>, val teamDisciplineFlow: StateFlow<List<TeamDiscipline>>) : State
    }
}
