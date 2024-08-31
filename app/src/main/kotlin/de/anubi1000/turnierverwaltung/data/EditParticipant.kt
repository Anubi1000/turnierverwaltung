package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.util.Identifiable
import org.mongodb.kbson.ObjectId

@Stable
class EditParticipant(
    override val id: ObjectId = ObjectId(),
    name: String = "",
    gender: Participant.Gender = Participant.Gender.MALE,
    clubId: ObjectId? = null,
) : Identifiable {
    var name by mutableStateOf(name)
    var gender by mutableStateOf(gender)
    var clubId by mutableStateOf(clubId)
}

fun Participant.toEditParticipant() = EditParticipant(
    id = this.id,
    name = this.name,
    gender = this.gender,
    clubId = this.club!!.id
)