package de.anubi1000.turnierverwaltung.server.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface Message {
    val type: Type

    @Serializable
    enum class Type {
        @SerialName("set_tournament") SET_TOURNAMENT
    }
}
