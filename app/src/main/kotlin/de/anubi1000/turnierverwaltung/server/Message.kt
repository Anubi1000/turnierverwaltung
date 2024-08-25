package de.anubi1000.turnierverwaltung.server

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface Message {
    val type: Type

    @Serializable
    enum class Type {
        @SerialName("set_tournament") SET_TOURNAMENT
    }
}

@Serializable
data class SetTournamentMessage(
    val tournamentName: String,
    val disciplines: List<Discipline>
) : Message {
    @Required
    override val type: Message.Type = Message.Type.SET_TOURNAMENT

    @Serializable
    data class Discipline(
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