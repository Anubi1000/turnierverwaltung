package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import org.mongodb.kbson.ObjectId

class EditTeamDiscipline(
    val id: ObjectId = ObjectId(),
    name: String = "",
    basedOn: List<ObjectId> = emptyList(),
) {
    var name by mutableStateOf(name)
    val basedOn = basedOn.toMutableStateList()
}

fun TeamDiscipline.toEditTeamDiscipline() = EditTeamDiscipline(
    id = id,
    name = name,
    basedOn = basedOn.map { it.id },
)
