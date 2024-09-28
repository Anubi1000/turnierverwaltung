package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.Composable
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
import io.realm.kotlin.types.annotations.Ignore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.mongodb.kbson.ObjectId

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

                fun toParagraphAlignment() = when (this) {
                    LEFT -> ParagraphAlignment.LEFT
                    CENTER -> ParagraphAlignment.CENTER
                    RIGHT -> ParagraphAlignment.RIGHT
                }
            }
        }

        @Serializable
        data class Row(
            @Ignore val id: ObjectId = ObjectId(),
            val values: List<String>,
        )
    }
}

fun Tournament.toScoreboardData(): ScoreboardData {
    val tables = mutableListOf<ScoreboardData.Table>()

    disciplines.forEach { discipline ->
        val columns = createDisciplineColumns(discipline)

        val results = participants.associateWith { participant ->
            ScoreCalculationUtils.getScoreForParticipantAllRounds(participant, discipline)
                ?.sortedDescending()
                ?: emptyList()
        }

        val rows = createParticipantRows(results, discipline)
        val maxResults = results.maxOfOrNull { it.value.size } ?: 0

        if (discipline.isGenderSeparated) {
            createGenderSeparatedTables(discipline, rows, results, maxResults).forEach { table ->
                tables.add(table)
            }
        } else {
            val sortedRows = rows.sortedWith(compareByResults(results, maxResults))
            tables.add(createTable(discipline.name, columns, sortedRows))
        }
    }

    teamDisciplines.forEach { teamDiscipline ->
        val columns = createTeamDisciplineColumns(teamSize)
        val results = calculateTeamResults(teamDiscipline)

        val rows = results.entries.sortedByDescending { it.value.first }.mapIndexed { index, result ->
            createTeamRow(index + 1, result)
        }

        tables.add(
            ScoreboardData.Table(
                name = teamDiscipline.name,
                columns = columns,
                rows = rows,
            ),
        )
    }

    tables.sortBy { it.name }

    return ScoreboardData(
        name = this.name,
        tables = tables,
    )
}

private fun createDisciplineColumns(discipline: Discipline): List<ScoreboardData.Table.Column> {
    val columns = mutableListOf(
        ScoreboardData.Table.Column("Platz", ScoreboardData.Table.Column.Width.Fixed(125), ScoreboardData.Table.Column.Alignment.CENTER),
        ScoreboardData.Table.Column("Startnummer", ScoreboardData.Table.Column.Width.Fixed(225), ScoreboardData.Table.Column.Alignment.CENTER),
        ScoreboardData.Table.Column("Name", ScoreboardData.Table.Column.Width.Variable(1f), ScoreboardData.Table.Column.Alignment.LEFT),
        ScoreboardData.Table.Column("Verein", ScoreboardData.Table.Column.Width.Variable(1f), ScoreboardData.Table.Column.Alignment.LEFT),
    )

    repeat(discipline.amountOfBestRoundsToShow) { i ->
        columns.add(ScoreboardData.Table.Column("Runde ${i + 1}", ScoreboardData.Table.Column.Width.Fixed(150), ScoreboardData.Table.Column.Alignment.RIGHT))
    }

    return columns
}

private fun createParticipantRows(results: Map<Participant, List<Double>>, discipline: Discipline): MutableList<ScoreboardData.Table.Row> {
    return results.mapNotNull { (participant, scores) ->
        if (scores.isEmpty()) return@mapNotNull null

        val values = mutableListOf(participant.startNumber.toString(), participant.name, participant.club?.name ?: "")
        repeat(discipline.amountOfBestRoundsToShow) { i ->
            values.add(scores.getOrNull(i)?.toString()?.replace('.', ',') ?: "")
        }

        ScoreboardData.Table.Row(id = participant.id, values = values)
    }.toMutableList()
}

private fun Tournament.compareByResults(results: Map<Participant, List<Double>>, maxResults: Int): Comparator<ScoreboardData.Table.Row> {
    return Comparator { first, second ->
        val firstResults = results.getValue(participants.first { it.id == first.id })
        val secondResults = results.getValue(participants.first { it.id == second.id })

        for (i in 0 until maxResults) {
            val firstScore = firstResults.getOrNull(i) ?: 0.0
            val secondScore = secondResults.getOrNull(i) ?: 0.0
            val compareResult = firstScore.compareTo(secondScore)
            if (compareResult != 0) return@Comparator -compareResult
        }
        0
    }
}

private fun Tournament.createGenderSeparatedTables(discipline: Discipline, rows: MutableList<ScoreboardData.Table.Row>, results: Map<Participant, List<Double>>, maxResults: Int): List<ScoreboardData.Table> = Participant.Gender.entries.mapNotNull { gender ->
    val filteredRows = rows.filter { row -> participants.first { it.id == row.id }.gender == gender }.toMutableList()
    if (filteredRows.isNotEmpty()) {
        filteredRows.sortWith(compareByResults(results, maxResults))
        val genderSuffix = when (gender) {
            Participant.Gender.MALE -> " (m)"
            Participant.Gender.FEMALE -> " (w)"
        }
        createTable(discipline.name + genderSuffix, createDisciplineColumns(discipline), filteredRows)
    } else {
        null
    }
}

private fun createTable(name: String, columns: List<ScoreboardData.Table.Column>, rows: List<ScoreboardData.Table.Row>): ScoreboardData.Table = ScoreboardData.Table(
    name = name,
    columns = columns,
    rows = rows.mapIndexed { index, row ->
        ScoreboardData.Table.Row(id = row.id, values = listOf((index + 1).toString()) + row.values)
    },
)

private fun createTeamDisciplineColumns(teamSize: Int): MutableList<ScoreboardData.Table.Column> {
    val columns = mutableListOf(
        ScoreboardData.Table.Column("Platz", ScoreboardData.Table.Column.Width.Fixed(150), ScoreboardData.Table.Column.Alignment.CENTER),
        ScoreboardData.Table.Column("Startnummer", ScoreboardData.Table.Column.Width.Fixed(200), ScoreboardData.Table.Column.Alignment.CENTER),
        ScoreboardData.Table.Column("Name", ScoreboardData.Table.Column.Width.Variable(1.0f), ScoreboardData.Table.Column.Alignment.LEFT),
    )

    repeat(teamSize) { i ->
        columns.add(ScoreboardData.Table.Column("Schütze ${i + 1}", ScoreboardData.Table.Column.Width.Variable(1f), ScoreboardData.Table.Column.Alignment.RIGHT))
        columns.add(ScoreboardData.Table.Column("Punkte", ScoreboardData.Table.Column.Width.Fixed(200), ScoreboardData.Table.Column.Alignment.LEFT))
    }

    columns.add(ScoreboardData.Table.Column("Gesamt", ScoreboardData.Table.Column.Width.Fixed(200), ScoreboardData.Table.Column.Alignment.RIGHT))
    return columns
}

private fun Tournament.calculateTeamResults(teamDiscipline: TeamDiscipline): Map<Team, Pair<Double, Map<Participant, Double>>> = teams.filter { it.participatingDisciplines.contains(teamDiscipline) }.associateWith { team ->
    val memberScores = team.members.associateWith { member ->
        teamDiscipline.basedOn
            .mapNotNull { discipline -> ScoreCalculationUtils.getScoreForParticipant(member, discipline) }
            .maxOrNull() ?: 0.0
    }
    memberScores.values.sum() to memberScores
}

private fun createTeamRow(rank: Int, result: Map.Entry<Team, Pair<Double, Map<Participant, Double>>>): ScoreboardData.Table.Row {
    val (team, scoreData) = result
    val (totalScore, memberScores) = scoreData

    val values = mutableListOf(rank.toString(), team.startNumber.toString(), team.name)
    memberScores.forEach { (member, score) ->
        values.add(member.name)
        values.add(score.toString().replace(".", ","))
    }
    values.add(totalScore.toString().replace(".", ","))

    return ScoreboardData.Table.Row(id = team.id, values = values)
}
