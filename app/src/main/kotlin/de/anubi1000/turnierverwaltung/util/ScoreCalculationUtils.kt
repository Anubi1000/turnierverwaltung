package de.anubi1000.turnierverwaltung.util

import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant
import java.math.BigDecimal
import java.math.RoundingMode

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
            BigDecimal(points).setScale(5, RoundingMode.HALF_EVEN).toDouble()
        }.toDoubleArray()
    }

    fun getScoreForParticipant(participant: Participant, discipline: Discipline): Double? = getScoreForParticipantAllRounds(participant, discipline)?.maxOrNull()
}
