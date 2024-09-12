package de.anubi1000.turnierverwaltung.viewmodel.discipline

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.EditTeamDiscipline
import de.anubi1000.turnierverwaltung.data.repository.DisciplineRepository
import de.anubi1000.turnierverwaltung.data.repository.TeamDisciplineRepository
import de.anubi1000.turnierverwaltung.data.toEditTeamDiscipline
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class TeamDisciplineEditViewModel(
    private val teamDisciplineRepository: TeamDisciplineRepository,
    private val disciplineRepository: DisciplineRepository,
    @InjectedParam private val tournamentId: ObjectId,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    private var isEditMode = false

    fun loadCreate() {
        viewModelScope.launch {
            val teamDiscipline = EditTeamDiscipline()
            state = State.Loaded(
                item = teamDiscipline,
                isValid = getValidationState(teamDiscipline),
                disciplines = disciplineRepository.getAllForTournament(tournamentId),
            )
            isEditMode = false
        }
    }

    fun loadEdit(id: ObjectId) {
        viewModelScope.launch {
            val club = teamDisciplineRepository.getById(id)!!.toEditTeamDiscipline()
            state = State.Loaded(
                item = club,
                isValid = getValidationState(club),
                disciplines = disciplineRepository.getAllForTournament(tournamentId),
            )
            isEditMode = true
        }
    }

    fun saveChanges(onSaved: (ObjectId) -> Unit) {
        val currentState = state
        require(currentState is State.Loaded && currentState.isValid.value)

        viewModelScope.launch {
            val teamDiscipline = TeamDiscipline(
                id = currentState.item.id,
                name = currentState.item.name,
                basedOn = currentState.item.basedOn.toRealmList(),
            )

            if (!isEditMode) {
                teamDisciplineRepository.insert(teamDiscipline, tournamentId)
            } else {
                teamDisciplineRepository.update(teamDiscipline)
            }
            onSaved(teamDiscipline.id)
        }
    }

    private fun getValidationState(teamDiscipline: EditTeamDiscipline): ComposeState<Boolean> = derivedStateOf {
        teamDiscipline.name.isNotBlank() &&
            teamDiscipline.basedOn.isNotEmpty()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(
            val item: EditTeamDiscipline,
            val isValid: ComposeState<Boolean>,
            val disciplines: List<Discipline>,
        ) : State
    }
}
