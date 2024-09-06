package de.anubi1000.turnierverwaltung.viewmodel.team

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.EditTeam
import de.anubi1000.turnierverwaltung.data.repository.ParticipantRepository
import de.anubi1000.turnierverwaltung.data.repository.TeamRepository
import de.anubi1000.turnierverwaltung.data.toEditTeam
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Team
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class TeamEditViewModel(
    private val teamRepository: TeamRepository,
    private val participantRepository: ParticipantRepository,
    @InjectedParam private val tournamentId: ObjectId
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    private var isEditMode = false

    fun loadCreate() {
        viewModelScope.launch {
            val team = EditTeam()
            state = State.Loaded(
                item = team,
                participants = participantRepository.getAllForTournament(tournamentId),
                isValid = getValidationState(team)
            )
            isEditMode = false
        }
    }

    fun loadEdit(id: ObjectId) {
        viewModelScope.launch {
            val team = teamRepository.getById(id)!!.toEditTeam()
            state = State.Loaded(
                item = team,
                participants = participantRepository.getAllForTournament(tournamentId),
                isValid = getValidationState(team)
            )
            isEditMode = true
        }
    }

    fun saveChanges(onSaved: (ObjectId) -> Unit) {
        val currentState = state
        require(currentState is State.Loaded && currentState.isValid.value)

        viewModelScope.launch {
            val team = Team().also {
                it.id = currentState.item.id
                it.name = currentState.item.name
                it.startNumber = currentState.item.startNumber

                it.members = currentState.item.members.map { id -> currentState.participants.find { participant -> participant.id == id }!! }.toRealmList()
            }

            if (!isEditMode) {
                teamRepository.insert(team, tournamentId)
            } else {
                teamRepository.update(team)
            }
            onSaved(team.id)
        }
    }

    private fun getValidationState(team: EditTeam): ComposeState<Boolean> = derivedStateOf {
        team.name.isNotBlank() &&
                team.members.isNotEmpty()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(
            val item: EditTeam,
            val participants: List<Participant>,
            val isValid: ComposeState<Boolean>
        ) : State
    }
}