package de.anubi1000.turnierverwaltung.server.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface Message {
    val type: Type

    @Serializable
    enum class Type {
        @SerialName("set_tournament")
        SET_TOURNAMENT,

        @SerialName("update_row")
        UPDATE_ROW,

        @SerialName("resend_tournament")
        RESEND_TOURNAMENT,

        ;

        companion object {
            fun fromString(str: String) = when (str) {
                "set_tournament" -> SET_TOURNAMENT
                "update_row" -> UPDATE_ROW
                "resend_tournament" -> RESEND_TOURNAMENT
                else -> null
            }
        }
    }
}
