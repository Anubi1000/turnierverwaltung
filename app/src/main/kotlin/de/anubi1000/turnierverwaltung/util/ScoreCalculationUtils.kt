package de.anubi1000.turnierverwaltung.util

import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant

object ScoreCalculationUtils {
    fun getScoreForParticipant(participant: Participant, discipline: Discipline): Double? {
        val disciplineResult = participant.results[discipline.id.toHexString()] ?: return null
        val score = disciplineResult.rounds.maxOf { round ->
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

        return score
    }
}
