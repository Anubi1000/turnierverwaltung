package de.anubi1000.turnierverwaltung.data.participant

import de.anubi1000.turnierverwaltung.database.model.ParticipantModel
import org.mongodb.kbson.ObjectId

data class Participant(
    val id: ObjectId = ObjectId(),
    val tournamentId: ObjectId = ObjectId(),
    val name: String = "",
    val verein: String = ""
)

fun ParticipantModel.toParticipant(): Participant = Participant(
    id = id,
    name = name,
    verein = verein
)
