package de.anubi1000.turnierverwaltung.server.messages

import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Tournament
import kotlinx.serialization.Required
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
        data class Column(val name: String, val width: String)

        @Serializable
        data class Row(
            val id: String,
            val values: List<String>
        )
    }
}

fun Tournament.toSetTournamentMessage(): SetTournamentMessage {
    return SetTournamentMessage(
        title = this.name,
        // Some temp data
        tables = IntRange(1, 5).map { tableIndex ->
            SetTournamentMessage.Table(
                id = tableIndex.toString(),
                name = "Disziplin $tableIndex",
                columns = IntRange(1, 5).map { columnIndex ->
                    SetTournamentMessage.Table.Column(
                        name = "Value $columnIndex",
                        width = "20%"
                    )
                },
                rows = IntRange(1, 50).map { rowIndex ->
                    SetTournamentMessage.Table.Row(
                        id = rowIndex.toString(),
                        values = IntRange(1, 5).map { valueIndex ->
                            "Row $rowIndex, Value $valueIndex"
                        }
                    )
                }
            )
        }
    )
}
