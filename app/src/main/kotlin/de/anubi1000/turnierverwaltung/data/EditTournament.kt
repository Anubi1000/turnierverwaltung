package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.anubi1000.turnierverwaltung.data.validation.validateInt
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.util.Constants
import org.mongodb.kbson.ObjectId
import java.time.Instant

@Stable
class EditTournament(
    val id: ObjectId = ObjectId(),
    name: String = "",
    date: Instant = Instant.now(),
    teamSize: String = "3",
) {
    var name by mutableStateOf(name)
    var date by mutableStateOf(date)
    var teamSize by mutableStateOf(teamSize)

    fun toTournament() = Tournament(
        id = id,
        name = name,
        date = date,
        teamSize = validateInt(teamSize, min = Constants.MIN_TEAM_SIZE)!!,
    )
}

fun Tournament.toEditTournament() = EditTournament(
    id = id,
    name = name,
    date = date,
    teamSize = teamSize.toString(),
)
