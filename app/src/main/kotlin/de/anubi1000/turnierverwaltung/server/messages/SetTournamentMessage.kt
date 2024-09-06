package de.anubi1000.turnierverwaltung.server.messages

import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Tournament
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetTournamentMessage(
    val title: String,
    val tables: List<Table>
) : Message {
    @Required
    override val type: Message.Type = Message.Type.SET_TOURNAMENT

    @Serializable
    data class Table(
        val id: String,
        val name: String,
        val columns: List<Column>,
        val rows: List<Row>
    ) {
        @Serializable
        data class Column(
            val name: String,
            val width: String,
            val alignment: Alignment,
        ) {
            @Serializable
            enum class Alignment {
                @SerialName("left") LEFT,
                @SerialName("center") CENTER,
                @SerialName("right") RIGHT,
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

fun Tournament.toSetTournamentMessage(): SetTournamentMessage {
    return SetTournamentMessage(
        title = this.name,
        tables = this.disciplines.flatMap { discipline ->
            getDisciplineTables(discipline)
        }
    )
}

private fun Tournament.getDisciplineTables(discipline: Discipline): List<SetTournamentMessage.Table> {
    val columns = listOf(
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
        )
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
                }.map { getParticipantRow(it, discipline) }
            ),
            SetTournamentMessage.Table(
                id = discipline.id.toHexString(),
                name = discipline.name + " (w)",
                columns = columns,
                rows = participants.filter { participant ->
                    if (participant.gender != Participant.Gender.FEMALE) return@filter false
                    val disciplineResult = participant.results[discipline.id.toHexString()]
                    disciplineResult != null && disciplineResult.rounds.isNotEmpty()
                }.map { getParticipantRow(it, discipline) }
            )
        )
    } else {
        listOf(SetTournamentMessage.Table(
            id = discipline.id.toHexString(),
            name = discipline.name,
            columns = columns,
            rows = participants.filter { participant ->
                val disciplineResult = participant.results[discipline.id.toHexString()]
                disciplineResult != null && disciplineResult.rounds.isNotEmpty()
            }.map { getParticipantRow(it, discipline) }
        ))
    }
}

private fun getParticipantRow(participant: Participant, discipline: Discipline): SetTournamentMessage.Table.Row {
    val disciplineResult = participant.results[discipline.id.toHexString()]!!
    val points = disciplineResult.rounds.maxOf { round ->
        var points = 0.0
        discipline.values.forEach { disciplineValue ->
            val value = round.values[disciplineValue.id.toHexString()]!!
            if (disciplineValue.isAdded) {
                points += value
            } else {
                points -= value
            }
        }
        points
    }

    return SetTournamentMessage.Table.Row(
        id = participant.id.toHexString(),
        values = listOf(
            participant.name,
            participant.club!!.name,
            points.toString().replace('.', ',')
        ),
        sortValues = listOf(
            points
        )
    )
}
