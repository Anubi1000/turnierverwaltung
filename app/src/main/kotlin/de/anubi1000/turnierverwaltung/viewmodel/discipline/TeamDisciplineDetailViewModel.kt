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
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class TeamDisciplineDetailViewModel(
    private val teamDisciplineRepository: TeamDisciplineRepository
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    fun loadItem(id: ObjectId) {
        viewModelScope.launch {
            val discipline = teamDisciplineRepository.getById(id)!!
            state = State.Loaded(discipline)
        }
    }

    fun deleteItem(onDeleted: () -> Unit) {
        val currentState = state
        require(currentState is State.Loaded)

        viewModelScope.launch {
            teamDisciplineRepository.delete(currentState.item.id)
            onDeleted()
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val item: TeamDiscipline) : State
    }
}