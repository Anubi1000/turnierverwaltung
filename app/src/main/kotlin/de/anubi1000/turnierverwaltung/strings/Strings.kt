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
    val create: String = "Neu",
    val delete: String = "Löschen",
    val edit: String = "Bearbeiten",
    val save: String = "Speichern",

    val dateOfTournament: String = "Datum des Turniers",
    val general: String = "Allgemein",
    val name: String = "Name",

    val xDontExist: (name: String) -> String = { arg1 ->
        buildString {
            append("Es existieren noch keine ")
            append(arg1)
        }
    },
    val wantToDeleteX: (name: String) -> AnnotatedString = { arg1 ->
        buildAnnotatedString {
            append("Möchtest du ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(arg1)
            }
            append(" wirklich löschen?")
        }
    },
    val editScreenTitle: (isEditMode: Boolean, name: String) -> String = { arg1, arg2 ->
        buildString {
            append(arg2)
            if (arg1) {
                append(" bearbeiten")
            } else {
                append(" erstellen")
            }
        }
    },

    val tournament: String = "Turnier",
    val tournaments: String = "Turniere",
    val noTournamentSelected: String = "Kein Turnier ausgewählt",
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
    val participants: String = "Teilnehmer",
    val noParticipantSelected: String = "Keinen Teilnehmer ausgewählt",
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

    val team: String = "Team",
    val teams: String = "Teams",
    val noTeamSelected: String = "Kein Team ausgewählt",

    val club: String = "Verein",
    val clubs: String = "Vereine",
    val noClubSelected: String = "Kein Verein ausgewählt",
    val createClub: String = "Verein erstellen",
    val editClub: String = "Verein bearbeiten",

    val scoreboard: String = "Scoreboard",
    val showOnScoreboard: String = "Auf Scoreboard anzeigen",
)
