package de.anubi1000.turnierverwaltung.viewmodel.team

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.TeamRepository
import de.anubi1000.turnierverwaltung.database.model.Team
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class TeamDetailViewModel(
    private val teamRepository: TeamRepository
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    fun loadItem(id: ObjectId) {
        viewModelScope.launch {
            val team = teamRepository.getById(id)!!
            state = State.Loaded(team)
        }
    }

    fun deleteItem(onDeleted: () -> Unit) {
        val currentState = state
        require(currentState is State.Loaded)

        viewModelScope.launch {
            teamRepository.delete(currentState.item.id)
            onDeleted()
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val item: Team) : State
    }
}