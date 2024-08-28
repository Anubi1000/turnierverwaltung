package de.anubi1000.turnierverwaltung.strings

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

data class Strings(
    val appName: String = "Turnierverwaltung",

    val back: String = "Zurück",
    val cancel: String = "Abbrechen",
    val confirm: String = "Bestätigen",
    val delete: String = "Löschen",
    val edit: String = "Bearbeiten",
    val save: String = "Speichern",

    val dateOfTournament: String = "Datum des Turniers",
    val general: String = "Allgemein",
    val name: String = "Name",

    val tournament: String = "Turnier",
    val tournaments: String = "Turniere",
    val noTournamentSelected: String = "Kein Turnier ausgewählt",
    val noTournamentsExist: String = "Es existieren noch keine Turniere",
    val createTournament: String = "Turnier erstellen",
    val deleteTournament: String = "Turnier löschen",
    val editTournament: String = "Turnier bearbeiten",
    val openTournament: String = "Turnier öffnen",
    val wantToDeleteTournament: (name: String) -> AnnotatedString = { arg1 ->
        buildAnnotatedString {
            append("Möchtest du das Turnier ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(arg1)
            }
            append(" wirklich löschen?")
        }
    },

    val participant: String = "Teilnehmer",
    val participants: String = participant,
    val noParticipantSelected: String = "Keinen Teilnehmer ausgewählt",
    val noParticipantsExist: String = "Es existieren noch keine Teilnehmer",
    val createParticipant: String = "Teilnehmer erstellen",
    val deleteParticipant: String = "Teilnehmer löschen",
    val editParticipant: String = "Teilnehmer bearbeiten",
    val wantToDeleteParticipant: (name: String) -> AnnotatedString = { arg1 ->
        buildAnnotatedString {
            append("Möchtest du den Teilnehmer ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(arg1)
            }
            append(" wirklich löschen?")
        }
    },

    val scoreboard: String = "Scoreboard",
    val showOnScoreboard: String = "Auf Scoreboard anzeigen",
)
