package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.anubi1000.turnierverwaltung.database.model.Tournament
import org.mongodb.kbson.ObjectId
import java.time.Instant

@Stable
class EditTournament(
    val id: ObjectId = ObjectId(),
    name: String = "",
    date: Instant = Instant.now(),
) {
    var name by mutableStateOf(name)
    var date by mutableStateOf(date)

    fun toTournament() = Tournament(
        id = id,
        name = name,
        date = date,
    )
}

fun Tournament.toEditTournament() = EditTournament(
    id = id,
    name = name,
    date = date,
)
