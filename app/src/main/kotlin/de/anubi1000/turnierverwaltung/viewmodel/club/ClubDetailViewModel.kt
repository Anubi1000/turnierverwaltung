package de.anubi1000.turnierverwaltung.viewmodel.club

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.repository.ClubRepository
import de.anubi1000.turnierverwaltung.database.model.Club
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class ClubDetailViewModel(
    private val clubRepository: ClubRepository,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    fun loadItem(id: ObjectId) {
        viewModelScope.launch {
            val club = clubRepository.getById(id)!!
            state = State.Loaded(club)
        }
    }

    fun deleteItem(onDeleted: () -> Unit) {
        val currentState = state
        require(currentState is State.Loaded)

        viewModelScope.launch {
            clubRepository.delete(currentState.item.id)
            onDeleted()
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val item: Club) : State
    }
}
