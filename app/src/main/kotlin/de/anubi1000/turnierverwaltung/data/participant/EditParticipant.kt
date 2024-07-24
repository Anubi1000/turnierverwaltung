package de.anubi1000.turnierverwaltung.data.participant

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.mongodb.kbson.ObjectId

@Stable
class EditParticipant(
    val id: ObjectId = ObjectId(),
    name: String = "",
    verein: String = ""
) {
    var name by mutableStateOf(name)
    var verein by mutableStateOf(verein)
}

fun Participant.toEditParticipant() = EditParticipant(
    id = id,
    name = name,
    verein = verein
)