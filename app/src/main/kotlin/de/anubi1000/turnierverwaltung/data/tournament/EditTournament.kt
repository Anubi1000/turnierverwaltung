package de.anubi1000.turnierverwaltung.data.tournament

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import de.anubi1000.turnierverwaltung.database.model.Tournament
import org.mongodb.kbson.ObjectId
import java.time.Instant

@Stable
class EditTournament(
    val id: ObjectId = ObjectId(),
    name: String = "",
    date: Instant = Instant.now(),
) {
    private var _name = mutableStateOf(name)
    private var _date = mutableStateOf(date)

    private var isEditable = true

    val name: String by _name
    fun setName(name: String) {
        if (isEditable) _name.value = name
    }

    val date: Instant by _date
    fun setDate(date: Instant) {
        if (isEditable) _date.value = date
    }

    fun blockEditing() {
        isEditable = false
    }

    fun toTournament(): Tournament {
        return Tournament().apply {
            id = this@EditTournament.id
            name = _name.value
            date = _date.value
        }
    }
}

fun Tournament.toEditTournament() = EditTournament(
    id = id,
    name = name,
    date = date
)
