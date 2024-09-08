package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.util.toObjectId
import io.realm.kotlin.ext.toRealmDictionary
import io.realm.kotlin.ext.toRealmList
import org.mongodb.kbson.ObjectId

@Stable
class EditParticipantResult(
    rounds: List<RoundResult> = emptyList(),
) {
    val rounds = rounds.toMutableStateList()

    @Stable
    class RoundResult(
        values: Map<ObjectId, Double> = emptyMap(),
    ) {
        val values = SnapshotStateMap<ObjectId, Double>().apply { putAll(values) }
    }

    fun toDisciplineResult() = Participant.DisciplineResult(
        rounds = rounds.map { round ->
            Participant.RoundResult(
                values = round.values.map { it.key.toHexString() to it.value }.toRealmDictionary(),
            )
        }.toRealmList(),
    )
}

fun Participant.DisciplineResult.toEditParticipantResult() = EditParticipantResult(
    rounds = rounds.map { round ->
        EditParticipantResult.RoundResult(
            values = round.values.map {
                it.key.toObjectId() to it.value
            }.toMap(),
        )
    },
)
