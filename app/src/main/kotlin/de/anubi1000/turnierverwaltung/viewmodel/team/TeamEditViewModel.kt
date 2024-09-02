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
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
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
            val participants = participantRepository.getAllForTournament(tournamentId)

            isEditMode = false

            state = State.Loaded(team, getValidationState(team), participants)
        }
    }

    fun loadEdit(id: ObjectId) {
        viewModelScope.launch {
            val team = teamRepository.getTeamById(id)!!.toEditTeam()
            val participants = participantRepository.getAllForTournament(tournamentId)

            isEditMode = true

            state = State.Loaded(team, getValidationState(team), participants)
        }
    }

    private fun getValidationState(team: EditTeam): ComposeState<Boolean> {
        return derivedStateOf {
            team.name.isNotBlank() &&
                    team.members.isNotEmpty()
        }
    }

    fun saveChanges(onSaved: (ObjectId) -> Unit) {
        val currentState = state
        require(currentState is State.Loaded && currentState.isValid.value)

        viewModelScope.launch {
            val team = Team().apply {
                id = currentState.team.id
                name = currentState.team.name
                startNumber = currentState.team.startNumber
                members = currentState.team.members.map { memberId -> currentState.participants.find { it.id == memberId }!! }.toRealmList()
            }

            if (isEditMode) {
                teamRepository.updateTeam(team)
            } else {
                teamRepository.insertTeam(
                    team = team,
                    tournamentId = tournamentId
                )
            }
            onSaved(currentState.team.id)
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(
            val team: EditTeam,
            val isValid: ComposeState<Boolean>,
            val participants: List<Participant>
        ) : State
    }
}