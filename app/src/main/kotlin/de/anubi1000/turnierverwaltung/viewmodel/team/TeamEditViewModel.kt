package de.anubi1000.turnierverwaltung.viewmodel.team

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.edit.EditTeam
import de.anubi1000.turnierverwaltung.data.edit.toEditTeam
import de.anubi1000.turnierverwaltung.data.repository.ParticipantRepository
import de.anubi1000.turnierverwaltung.data.repository.TeamDisciplineRepository
import de.anubi1000.turnierverwaltung.data.repository.TeamRepository
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.data.validation.validateName
import de.anubi1000.turnierverwaltung.data.validation.validateStartNumber
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class TeamEditViewModel(
    private val teamRepository: TeamRepository,
    private val participantRepository: ParticipantRepository,
    private val teamDisciplineRepository: TeamDisciplineRepository,
    private val tournamentRepository: TournamentRepository,
    @InjectedParam private val tournamentId: ObjectId,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    private var isEditMode = false

    fun loadCreate() {
        viewModelScope.launch {
            val team = EditTeam()
            val tournament = tournamentRepository.getById(tournamentId)!!

            state = State.Loaded(
                item = team,
                participants = participantRepository.getAllForTournamentAsFlow(tournamentId).first(),
                teamDisciplines = teamDisciplineRepository.getAllForTournamentAsFlow(tournamentId).first(),
                isValid = getValidationState(team, tournament.teamSize),
            )
            isEditMode = false
        }
    }

    fun loadEdit(id: ObjectId) {
        viewModelScope.launch {
            val team = teamRepository.getById(id)!!.toEditTeam()
            val tournament = tournamentRepository.getById(tournamentId)!!

            state = State.Loaded(
                item = team,
                participants = participantRepository.getAllForTournamentAsFlow(tournamentId).first(),
                teamDisciplines = teamDisciplineRepository.getAllForTournamentAsFlow(tournamentId).first(),
                isValid = getValidationState(team, tournament.teamSize),
            )
            isEditMode = true
        }
    }

    fun saveChanges(onSaved: (ObjectId) -> Unit) {
        val currentState = state
        require(currentState is State.Loaded && currentState.isValid.value)

        viewModelScope.launch {
            val team = Team(
                id = currentState.item.id,
                name = validateName(currentState.item.name)!!,
                startNumber = validateStartNumber(currentState.item.startNumber)!!,

                members = currentState.item.members.toRealmList(),
                participatingDisciplines = currentState.item.participatingDisciplines.toRealmList(),
            )

            if (!isEditMode) {
                teamRepository.insert(team, tournamentId)
            } else {
                teamRepository.update(team)
            }
            onSaved(team.id)
        }
    }

    private fun getValidationState(team: EditTeam, teamSize: Int): ComposeState<Boolean> = derivedStateOf {
        validateName(team.name) != null &&
            team.members.size >= teamSize &&
            validateStartNumber(team.startNumber) != null
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(
            val item: EditTeam,
            val participants: List<Participant>,
            val teamDisciplines: List<TeamDiscipline>,
            val isValid: ComposeState<Boolean>,
        ) : State
    }
}
