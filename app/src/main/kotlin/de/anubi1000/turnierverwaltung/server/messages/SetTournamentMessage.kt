package de.anubi1000.turnierverwaltung.server.messages

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
            val sortValues: List<Int>,
        )
    }
}

fun Tournament.toSetTournamentMessage(): SetTournamentMessage {
    return SetTournamentMessage(
        title = this.name,
        // Some temp data
        tables = this.disciplines.flatMap { discipline ->
            if (discipline.isGenderSeparated) {
                listOf(
                    SetTournamentMessage.Table(
                        id = discipline.id.toHexString(),
                        name = discipline.name + " (m)",
                        columns = IntRange(1, 5).map { columnIndex ->
                            SetTournamentMessage.Table.Column(
                                name = "Value $columnIndex",
                                width = "20%",
                                alignment = when (columnIndex) {
                                    1 -> SetTournamentMessage.Table.Column.Alignment.LEFT
                                    5 -> SetTournamentMessage.Table.Column.Alignment.RIGHT
                                    else -> SetTournamentMessage.Table.Column.Alignment.CENTER
                                }
                            )
                        },
                        rows = participants.filter { it.gender == Participant.Gender.MALE }.map { participant ->
                            SetTournamentMessage.Table.Row(
                                id = participant.id.toHexString(),
                                values = IntRange(1, 5).map { valueIndex ->
                                    "Row ${participant.name}, Value $valueIndex"
                                },
                                sortValues = listOf(participant.startNumber)
                            )
                        }
                    ),
                    SetTournamentMessage.Table(
                        id = discipline.id.toHexString(),
                        name = discipline.name + " (w)",
                        columns = IntRange(1, 5).map { columnIndex ->
                            SetTournamentMessage.Table.Column(
                                name = "Value $columnIndex",
                                width = "20%",
                                alignment = when (columnIndex) {
                                    1 -> SetTournamentMessage.Table.Column.Alignment.LEFT
                                    5 -> SetTournamentMessage.Table.Column.Alignment.RIGHT
                                    else -> SetTournamentMessage.Table.Column.Alignment.CENTER
                                }
                            )
                        },
                        rows = participants.filter { it.gender == Participant.Gender.FEMALE }.map { participant ->
                            SetTournamentMessage.Table.Row(
                                id = participant.id.toHexString(),
                                values = IntRange(1, 5).map { valueIndex ->
                                    "Row ${participant.name}, Value $valueIndex"
                                },
                                sortValues = listOf(participant.startNumber)
                            )
                        }
                    )
                )
            } else {
                listOf(SetTournamentMessage.Table(
                    id = discipline.id.toHexString(),
                    name = discipline.name,
                    columns = IntRange(1, 5).map { columnIndex ->
                        SetTournamentMessage.Table.Column(
                            name = "Value $columnIndex",
                            width = "20%",
                            alignment = when (columnIndex) {
                                1 -> SetTournamentMessage.Table.Column.Alignment.LEFT
                                5 -> SetTournamentMessage.Table.Column.Alignment.RIGHT
                                else -> SetTournamentMessage.Table.Column.Alignment.CENTER
                            }
                        )
                    },
                    rows = participants.map { participant ->
                        SetTournamentMessage.Table.Row(
                            id = participant.id.toHexString(),
                            values = IntRange(1, 5).map { valueIndex ->
                                "Row ${participant.name}, Value $valueIndex"
                            },
                            sortValues = listOf(participant.startNumber)
                        )
                    }
                ))
            }
        }
    )
}
