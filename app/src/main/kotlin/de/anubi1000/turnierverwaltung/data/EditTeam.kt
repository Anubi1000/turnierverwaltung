package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import org.mongodb.kbson.ObjectId

@Stable
class EditTeam(
    val id: ObjectId = ObjectId(),
    name: String = "",
    startNumber: Int = 0,
    members: List<Participant> = emptyList(),
    participatingDisciplines: List<TeamDiscipline> = emptyList(),
) {
    var name by mutableStateOf(name)
    var startNumber by mutableIntStateOf(startNumber)
    val members = members.toMutableStateList()
    val participatingDisciplines = participatingDisciplines.toMutableStateList()
}

fun Team.toEditTeam() = EditTeam(
    id = id,
    name = name,
    startNumber = startNumber,
    members = members,
    participatingDisciplines = participatingDisciplines,
)
