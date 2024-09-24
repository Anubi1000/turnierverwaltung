package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.util.ScoreCalculationUtils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScoreboardData(
    val name: String,
    val tables: List<Table>,
) {
    @Serializable
    data class Table(
        val name: String,
        val columns: List<Column>,
        val rows: List<Row>,
    ) {
        @Serializable
        data class Column(
            val name: String,
            val width: Width,
            val alignment: Alignment,
        ) {
            @Serializable
            sealed interface Width {
                @Serializable
                data class Fixed(val width: Int) : Width {
                    @Composable
                    fun toDp(): Dp {
                        val density = LocalDensity.current
                        return remember(density) {
                            with(density) {
                                width.toDp()
                            }
                        }
                    }
                }

                @Serializable
                data class Variable(val weight: Float) : Width
            }

            @Serializable
            enum class Alignment {
                @SerialName("left")
                LEFT,
                @SerialName("center")
                CENTER,
                @SerialName("right")
                RIGHT,
                ;

                fun toComposeTextAlignment(): TextAlign = when (this) {
                    LEFT -> TextAlign.Left
                    CENTER -> TextAlign.Center
                    RIGHT -> TextAlign.Right
                }
            }
        }

        @Serializable
        data class Row(
            val values: List<String>,
        )
    }
}

fun Tournament.toScoreboardData(): ScoreboardData = ScoreboardData(
    name = this.name,
    tables = (
        this.disciplines.flatMap { discipline ->
            getDisciplineTables(discipline)
        } + this.teamDisciplines.map { teamDiscipline ->
            getTeamDisciplineTables(teamDiscipline)
        }
        ),
)

private fun Tournament.getTeamDisciplineTables(teamDiscipline: TeamDiscipline): ScoreboardData.Table {
    val columns = mutableListOf(
        ScoreboardData.Table.Column(
            name = "Startnummer",
            width = ScoreboardData.Table.Column.Width.Fixed(250),
            alignment = ScoreboardData.Table.Column.Alignment.LEFT,
        ),
        ScoreboardData.Table.Column(
            name = "Name",
            width = ScoreboardData.Table.Column.Width.Variable(1.0f),
            alignment = ScoreboardData.Table.Column.Alignment.LEFT,
        ),
    )

    for (i in 1..teamSize) {
        columns.add(
            ScoreboardData.Table.Column(
                name = "Schütze $i",
                width = ScoreboardData.Table.Column.Width.Variable(1f),
                alignment = ScoreboardData.Table.Column.Alignment.RIGHT,
            ),
        )
        columns.add(
            ScoreboardData.Table.Column(
                name = "Punkte",
                width = ScoreboardData.Table.Column.Width.Fixed(250),
                alignment = ScoreboardData.Table.Column.Alignment.LEFT,
            ),
        )
    }

    columns.add(
        ScoreboardData.Table.Column(
            name = "Gesamt",
            width = ScoreboardData.Table.Column.Width.Fixed(250),
            alignment = ScoreboardData.Table.Column.Alignment.RIGHT,
        ),
    )

    return ScoreboardData.Table(
        name = teamDiscipline.name,
        columns = columns,
        rows = teams.filter { team -> team.participatingDisciplines.any { it.id == teamDiscipline.id } }.map { team ->
            getTeamRow(team, teamDiscipline)
        },
    )
}

private fun getTeamRow(team: Team, teamDiscipline: TeamDiscipline): ScoreboardData.Table.Row {
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

    return ScoreboardData.Table.Row(
        values = values,
    )
}

private fun Tournament.getDisciplineTables(discipline: Discipline): List<ScoreboardData.Table> {
    val columns = mutableListOf(
        ScoreboardData.Table.Column(
            name = "Startnummer",
            width = ScoreboardData.Table.Column.Width.Fixed(250),
            alignment = ScoreboardData.Table.Column.Alignment.LEFT,
        ),
        ScoreboardData.Table.Column(
            name = "Name",
            width = ScoreboardData.Table.Column.Width.Variable(1f),
            alignment = ScoreboardData.Table.Column.Alignment.LEFT,
        ),
        ScoreboardData.Table.Column(
            name = "Verein",
            width = ScoreboardData.Table.Column.Width.Variable(1f),
            alignment = ScoreboardData.Table.Column.Alignment.LEFT,
        ),
    )

    for (i in 1..discipline.amountOfBestRoundsToShow) {
        columns.add(
            ScoreboardData.Table.Column(
                name = "Runde $i",
                width = ScoreboardData.Table.Column.Width.Fixed(150),
                alignment = ScoreboardData.Table.Column.Alignment.RIGHT,
            ),
        )
    }

    return if (discipline.isGenderSeparated) {
        listOf(
            ScoreboardData.Table(
                name = discipline.name + " (m)",
                columns = columns,
                rows = participants.filter { participant ->
                    if (participant.gender != Participant.Gender.MALE) return@filter false
                    val disciplineResult = participant.results[discipline.id.toHexString()]
                    disciplineResult != null && disciplineResult.rounds.isNotEmpty()
                }.map { getParticipantRow(it, discipline) },
            ),
            ScoreboardData.Table(
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
            ScoreboardData.Table(
                name = discipline.name,
                columns = columns,
                rows = participants.filter { participant ->
                    val disciplineResult = participant.results[discipline.id.toHexString()]
                    disciplineResult != null && disciplineResult.rounds.isNotEmpty()
                }.map { getParticipantRow(it, discipline) }.toImmutableList(),
            ),
        )
    }
}

private fun getParticipantRow(participant: Participant, discipline: Discipline): ScoreboardData.Table.Row {
    val allPoints = ScoreCalculationUtils.getScoreForParticipantAllRounds(participant, discipline)!!
    allPoints.sortDescending()

    val values = mutableListOf(
        participant.startNumber.toString(),
        participant.name,
        participant.club!!.name,
    )
    val sortValues = mutableListOf<Double>()

    for (i in 0 until discipline.amountOfBestRoundsToShow) {
        val points = allPoints.getOrNull(i)
        if (points != null) {
            values.add(points.toString().replace('.', ','))
            sortValues.add(points)
        } else {
            values.add("")
            sortValues.add(0.0)
        }
    }

    return ScoreboardData.Table.Row(
        values = values,
    )
}
