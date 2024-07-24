package de.anubi1000.turnierverwaltung.data.participant

import de.anubi1000.turnierverwaltung.database.model.ParticipantModel
import org.mongodb.kbson.ObjectId

data class ListParticipant(
    val id: ObjectId = ObjectId(),
    val name: String = "",
    val verein: String = ""
)

fun ParticipantModel.toListParticipant() = ListParticipant(
    id = id,
    name = name,
    verein = verein
)
