package de.anubi1000.turnierverwaltung.server.messages

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
    val title: String,
    val tables: List<Table>,
) : Message {
    @Required
    override val type: Message.Type = Message.Type.SET_TOURNAMENT

    @Serializable
    data class Table(
        val id: String,
        val name: String,
        val columns: List<Column>,
        val rows: List<Row>,
    ) {
        @Serializable
        data class Column(
            val name: String,
            val width: String,
            val alignment: Alignment,
        ) {
            @Serializable
            enum class Alignment {
                @SerialName("left")
                LEFT,

                @SerialName("center")
                CENTER,

                @SerialName("right")
                RIGHT,
            }
        }

        @Serializable
        data class Row(
            val id: String,
            val values: List<String>,
            val sortValues: List<Double>,
        )
    }
}

fun Tournament.toSetTournamentMessage(): SetTournamentMessage = SetTournamentMessage(
    title = this.name,
    tables = this.disciplines.flatMap { discipline ->
        getDisciplineTables(discipline)
    } + this.teamDisciplines.map { teamDiscipline ->
        getTeamDisciplineTables(teamDiscipline)
    },
)

private fun Tournament.getTeamDisciplineTables(teamDiscipline: TeamDiscipline): SetTournamentMessage.Table {
    val columnAmount = teamSize + 1
    val columnSpace = 100 / columnAmount

    val columns = mutableListOf(
        SetTournamentMessage.Table.Column(
            name = "Startnummer",
            width = "250px",
            alignment = SetTournamentMessage.Table.Column.Alignment.LEFT,
        ),
        SetTournamentMessage.Table.Column(
            name = "Name",
            width = "$columnSpace%",
            alignment = SetTournamentMessage.Table.Column.Alignment.LEFT,
        ),
    )

    for (i in 1..teamSize) {
        columns.add(
            SetTournamentMessage.Table.Column(
                name = "Person $i",
                width = "$columnSpace%",
                alignment = SetTournamentMessage.Table.Column.Alignment.RIGHT,
            ),
        )
        columns.add(SetTournamentMessage.Table.Column(
            name = "Punkte",
            width = "250px",
            alignment = SetTournamentMessage.Table.Column.Alignment.LEFT,
        ))
    }

    columns.add(
        SetTournamentMessage.Table.Column(
            name = "Gesamt",
            width = "250px",
            alignment = SetTournamentMessage.Table.Column.Alignment.RIGHT,
        ),
    )

    return SetTournamentMessage.Table(
        id = teamDiscipline.id.toHexString(),
        name = teamDiscipline.name,
        columns = columns,
        rows = teams.filter { team -> team.participatingDisciplines.any { it.id == teamDiscipline.id } }.map { team ->
            getTeamRow(team, teamDiscipline)
        },
    )
}

private fun getTeamRow(team: Team, teamDiscipline: TeamDiscipline): SetTournamentMessage.Table.Row {
    var total = 0.0
    val values = mutableListOf(
        team.startNumber.toString(),
        team.name,
    )

    team.members.forEach { member ->
        var points = 0.0
        teamDiscipline.basedOn.forEach { discipline ->
            val disciplinePoints = ScoreCalculationUtils.getScoreForParticipant(member, discipline)
            if (disciplinePoints != null) {
                if (points < disciplinePoints) {
                    points = disciplinePoints
                }
            }
        }

        values.add(member.name)
        values.add(points.toString())
        total += points
    }

    values.add(total.toString())

    return SetTournamentMessage.Table.Row(
        id = team.id.toHexString(),
        values = values,
        sortValues = listOf(
            total,
        ),
    )
}

private fun Tournament.getDisciplineTables(discipline: Discipline): List<SetTournamentMessage.Table> {
    val columns = listOf(
        SetTournamentMessage.Table.Column(
            name = "Startnummer",
            width = "250px",
            alignment = SetTournamentMessage.Table.Column.Alignment.LEFT,
        ),
        SetTournamentMessage.Table.Column(
            name = "Name",
            width = "50%",
            alignment = SetTournamentMessage.Table.Column.Alignment.LEFT,
        ),
        SetTournamentMessage.Table.Column(
            name = "Verein",
            width = "50%",
            alignment = SetTournamentMessage.Table.Column.Alignment.LEFT,
        ),
        SetTournamentMessage.Table.Column(
            name = "Punkte",
            width = "250px",
            alignment = SetTournamentMessage.Table.Column.Alignment.RIGHT,
        ),
    )

    return if (discipline.isGenderSeparated) {
        listOf(
            SetTournamentMessage.Table(
                id = discipline.id.toHexString(),
                name = discipline.name + " (m)",
                columns = columns,
                rows = participants.filter { participant ->
                    if (participant.gender != Participant.Gender.MALE) return@filter false
                    val disciplineResult = participant.results[discipline.id.toHexString()]
                    disciplineResult != null && disciplineResult.rounds.isNotEmpty()
                }.map { getParticipantRow(it, discipline) },
            ),
            SetTournamentMessage.Table(
                id = discipline.id.toHexString(),
                name = discipline.name + " (w)",
                columns = columns,
                rows = participants.filter { participant ->
                    if (participant.gender != Participant.Gender.FEMALE) return@filter false
                    val disciplineResult = participant.results[discipline.id.toHexString()]
                    disciplineResult != null && disciplineResult.rounds.isNotEmpty()
                }.map { getParticipantRow(it, discipline) },
            ),
        )
    } else {
        listOf(
            SetTournamentMessage.Table(
                id = discipline.id.toHexString(),
                name = discipline.name,
                columns = columns,
                rows = participants.filter { participant ->
                    val disciplineResult = participant.results[discipline.id.toHexString()]
                    disciplineResult != null && disciplineResult.rounds.isNotEmpty()
                }.map { getParticipantRow(it, discipline) },
            ),
        )
    }
}

private fun getParticipantRow(participant: Participant, discipline: Discipline): SetTournamentMessage.Table.Row {
    val points = ScoreCalculationUtils.getScoreForParticipant(participant, discipline)!!

    return SetTournamentMessage.Table.Row(
        id = participant.id.toHexString(),
        values = listOf(
            participant.startNumber.toString(),
            participant.name,
            participant.club!!.name,
            points.toString().replace('.', ','),
        ),
        sortValues = listOf(
            points,
        ),
    )
}
