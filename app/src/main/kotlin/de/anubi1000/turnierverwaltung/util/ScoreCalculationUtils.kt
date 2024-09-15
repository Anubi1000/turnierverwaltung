package de.anubi1000.turnierverwaltung.util

import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant

object ScoreCalculationUtils {
    fun getScoreForParticipantAllRounds(participant: Participant, discipline: Discipline): DoubleArray? {
        val disciplineResult = participant.results[discipline.id.toHexString()] ?: return null
        return disciplineResult.rounds.map { round ->
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
        }.toDoubleArray()
    }

    fun getScoreForParticipant(participant: Participant, discipline: Discipline): Double? = getScoreForParticipantAllRounds(participant, discipline)?.maxOrNull()
}
