package de.anubi1000.turnierverwaltung.data.tournament

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.collections.immutable.toPersistentList
import org.mongodb.kbson.ObjectId
import java.time.Instant

@Stable
class EditTournament(
    val id: ObjectId = ObjectId(),
    name: String = "",
    date: Instant = Instant.now(),
) {
    var name by mutableStateOf(name)
    var date: Instant by mutableStateOf(date)
    val values = mutableStateListOf(Value())

    fun toTournament(): Tournament {
        return Tournament(
            id = id,
            name = name,
            date = date,
            values = values.map {
                Tournament.Value(id = it.id, name = it.name, subtract = it.subtract)
            }.toPersistentList()
        )
    }

    @Stable
    class Value(
        val id: ObjectId = ObjectId(),
        name: String = "",
        subtract: Boolean = false
    ) {
        var name by mutableStateOf(name)
        var subtract by mutableStateOf(subtract)
    }
}

fun Tournament.toEditTournament(): EditTournament = EditTournament(
    id = id,
    name = name,
    date = date,
).also { editTournament ->
    editTournament.values.clear()

    values.forEach { value ->
        editTournament.values.add(
            EditTournament.Value(
                id = value.id,
                name = value.name,
                subtract = value.subtract
            )
        )
    }
}