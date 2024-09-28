package de.anubi1000.turnierverwaltung.strings

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import de.anubi1000.turnierverwaltung.database.model.Participant

data class Strings(
    val appName: String = "Turnierverwaltung",

    val back: String = "Zurück",
    val cancel: String = "Abbrechen",
    val confirm: String = "Bestätigen",
    val create: String = "Neu",
    val delete: String = "Löschen",
    val edit: String = "Bearbeiten",
    val save: String = "Speichern",

    val amountOfResultsOnDisplay: String = "Anzahl Ergebnisse auf Anzeige",
    val basedOn: String = "Basiert auf",
    val dateOfTournament: String = "Datum des Turniers",
    val gender: String = "Geschlecht",
    val general: String = "Allgemein",
    val members: String = "Mitglieder",
    val name: String = "Name",
    val overview: String = "Übersicht",
    val participantsWithClub: String = "Teilnehmer mit diesem Verein",
    val search: String = "Suche",
    val searchTerm: String = "Suchbegriff",
    val startNumber: String = "Startnummer",
    val teamSize: String = "Teamgröße",

    val yesno: (Boolean) -> String = { arg1 ->
        if (arg1) "Ja" else "Nein"
    },

    val values: String = "Werte",
    val newValue: String = "Neuer Wert",
    val isAdded: String = "Wird addiert",
    val isAddedYesNo: (isAdded: Boolean) -> String = { arg1 ->
        buildString {
            append(isAdded)
            append(": ")
            append(yesno(arg1))
        }
    },

    val points: String = "Punkte",
    val inputPoints: String = "Punkte eintragen",
    val newRound: String = "Neue Runde",

    val genderSeparated: String = "Geschlechter getrennt",
    val genderName: (Participant.Gender) -> String = { arg1 ->
        when (arg1) {
            Participant.Gender.MALE -> "Männlich"
            Participant.Gender.FEMALE -> "Weiblich"
        }
    },

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

    val discipline: String = "Disziplin",
    val disciplines: String = "Disziplinen",
    val teamDiscipline: String = "Team-Disziplin",
    val teamDisciplines: String = "Team-Disziplinen",
    val newDiscipline: String = "Neue Disziplin",
    val noDisciplineSelected: String = "Keine Disziplin ausgewählt",
    val noDisciplineAvailable: String = "Keine Disziplinen vorhanden",
    val noTeamDisciplineAvailable: String = "Keine Team-Disziplinen vorhanden",

    val scoreboard: String = "Scoreboard",
    val showOnScoreboard: String = "Auf Scoreboard anzeigen",
)
