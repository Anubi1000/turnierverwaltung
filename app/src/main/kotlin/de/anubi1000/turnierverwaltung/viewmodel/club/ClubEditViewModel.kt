package de.anubi1000.turnierverwaltung.viewmodel.club

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.edit.EditClub
import de.anubi1000.turnierverwaltung.data.edit.toEditClub
import de.anubi1000.turnierverwaltung.data.repository.ClubRepository
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class ClubEditViewModel(
    private val clubRepository: ClubRepository,
    @InjectedParam private val tournamentId: ObjectId,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    private var isEditMode = false

    fun loadCreate() {
        val club = EditClub()
        state = State.Loaded(
            item = club,
            isValid = getValidationState(club),
        )
        isEditMode = false
    }

    fun loadEdit(id: ObjectId) {
        viewModelScope.launch {
            val club = clubRepository.getById(id)!!.toEditClub()
            state = State.Loaded(
                item = club,
                isValid = getValidationState(club),
            )
            isEditMode = true
        }
    }

    fun saveChanges(onSaved: (ObjectId) -> Unit) {
        val currentState = state
        require(currentState is State.Loaded && currentState.isValid.value)

        viewModelScope.launch {
            val club = currentState.item.toClub()
            if (!isEditMode) {
                clubRepository.insert(club, tournamentId)
            } else {
                clubRepository.update(club)
            }
            onSaved(club.id)
        }
    }

    private fun getValidationState(club: EditClub): ComposeState<Boolean> = derivedStateOf {
        club.name.isNotBlank()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val item: EditClub, val isValid: ComposeState<Boolean>) : State
    }
}
