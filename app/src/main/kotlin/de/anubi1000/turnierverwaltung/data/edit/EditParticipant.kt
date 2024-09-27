package de.anubi1000.turnierverwaltung.data.edit

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.anubi1000.turnierverwaltung.database.model.Participant
import org.mongodb.kbson.ObjectId

@Stable
class EditParticipant(
    val id: ObjectId = ObjectId(),
    name: String = "",
    startNumber: String = "0",
    gender: Participant.Gender = Participant.Gender.MALE,
    clubId: ObjectId? = null,
) {
    var name by mutableStateOf(name)
    var startNumber by mutableStateOf(startNumber)
    var gender by mutableStateOf(gender)
    var clubId by mutableStateOf(clubId)
}

fun Participant.toEditParticipant() = EditParticipant(
    id = this.id,
    name = this.name,
    startNumber = this.startNumber.toString(),
    gender = this.gender,
    clubId = this.club!!.id,
)
