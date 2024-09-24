package de.anubi1000.turnierverwaltung.server.messages

import de.anubi1000.turnierverwaltung.data.ScoreboardData
import de.anubi1000.turnierverwaltung.data.toScoreboardData
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.util.ScoreCalculationUtils
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetTournamentMessage(
    val data: ScoreboardData
) : Message {
    @Required
    override val type: Message.Type = Message.Type.SET_TOURNAMENT
}

fun Tournament.toSetTournamentMessage(): SetTournamentMessage = SetTournamentMessage(
    data = toScoreboardData(),
)
