package de.anubi1000.turnierverwaltung.viewmodel.discipline

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.edit.EditDiscipline
import de.anubi1000.turnierverwaltung.data.edit.toEditDiscipline
import de.anubi1000.turnierverwaltung.data.repository.DisciplineRepository
import de.anubi1000.turnierverwaltung.data.validation.validateAmountOfBestRoundsToShow
import de.anubi1000.turnierverwaltung.data.validation.validateName
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class DisciplineEditViewModel(
    private val disciplineRepository: DisciplineRepository,
    @InjectedParam private val tournamentId: ObjectId,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    private var isEditMode = false

    fun loadCreate() {
        val discipline = EditDiscipline()
        state = State.Loaded(
            item = discipline,
            isValid = getValidationState(discipline),
        )
        isEditMode = false
    }

    fun loadEdit(id: ObjectId) {
        viewModelScope.launch {
            val club = disciplineRepository.getById(id)!!.toEditDiscipline()
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
            val discipline = currentState.item.toDiscipline()
            if (!isEditMode) {
                disciplineRepository.insert(discipline, tournamentId)
            } else {
                disciplineRepository.update(discipline)
            }
            onSaved(discipline.id)
        }
    }

    private fun getValidationState(discipline: EditDiscipline): ComposeState<Boolean> = derivedStateOf {
        validateName(discipline.name) != null &&
            discipline.values.isNotEmpty() &&
            discipline.values.all { validateName(it.name) != null } &&
            validateAmountOfBestRoundsToShow(discipline.amountOfBestRoundsToShow) != null
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val item: EditDiscipline, val isValid: ComposeState<Boolean>) : State
    }
}
