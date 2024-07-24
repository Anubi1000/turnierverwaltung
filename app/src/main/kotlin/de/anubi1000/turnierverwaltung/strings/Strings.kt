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
    val createParticipant: String = "Teilnehmer erstellen",
    val createTournament: String = "Turnier erstellen",
    val dateOfTournament: String = "Datum des Turniers",
    val delete: String = "Löschen",
    val deleteParticipant: String = "Teilnehmer löschen",
    val deleteTournament: String = "Turnier löschen",
    val edit: String = "Bearbeiten",
    val editParticipant: String = "Teilnehmer bearbeiten",
    val editTournament: String = "Turnier bearbeiten",
    val general: String = "Allgemein",
    val isSubtracted: String = "Wird abgezogen",
    val name: String = "Name",
    val newValue: String = "Neuer Wert",
    val noParticipantsExist: String = "Es existieren noch keine Teilnehmer",
    val noTournamentSelected: String = "Kein Turnier ausgewählt",
    val noTournamentsExist: String = "Es existieren noch keine Turniere",
    val openTournament: String = "Turnier öffnen",
    val participants: String = "Teilnehmer",
    val save: String = "Speichern",
    val scoreboard: String = "Scoreboard",
    val subtract: String = "Abziehen",
    val tournament: String = "Turnier",
    val tournaments: String = "Turniere",
    val values: String = "Werte",
    val wantToDeleteParticipant: (name: String) -> AnnotatedString = {
        buildAnnotatedString {
            append("Möchtest du den Teilnehmer \"")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(name)
            }
            append("\" wirklich löschen?")
        }
    },
    val wantToDeleteTournament: (name: String) -> AnnotatedString = {
        buildAnnotatedString {
            append("Möchtest du das Turnier \"")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(name)
            }
            append("\" wirklich löschen?")
        }
    },
    val yesno: (Boolean) -> String = { if (it) "Ja" else "Nein" },
)
