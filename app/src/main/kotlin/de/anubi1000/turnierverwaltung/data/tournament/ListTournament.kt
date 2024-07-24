package de.anubi1000.turnierverwaltung.data.tournament

import de.anubi1000.turnierverwaltung.database.model.TournamentModel
import org.mongodb.kbson.ObjectId
import java.time.Instant

data class ListTournament(
    val id: ObjectId = ObjectId(),
    val name: String = "",
    val date: Instant = Instant.EPOCH
)

fun TournamentModel.toListTournament(): ListTournament {
    return ListTournament(
        id = id,
        name = name,
        date = date
    )
}
