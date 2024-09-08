package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import de.anubi1000.turnierverwaltung.database.model.Club
import org.mongodb.kbson.ObjectId

@Stable
class EditClub(
    val id: ObjectId = ObjectId(),
    name: String = "",
) {
    var name by mutableStateOf(name)

    fun toClub() = Club(
        id = id,
        name = name,
    )
}

fun Club.toEditClub() = EditClub(
    id = id,
    name = name,
)
