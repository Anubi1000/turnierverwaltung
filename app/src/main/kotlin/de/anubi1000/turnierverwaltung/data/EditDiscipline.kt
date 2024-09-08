package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import de.anubi1000.turnierverwaltung.database.model.Discipline
import org.mongodb.kbson.ObjectId

class EditDiscipline(
    val id: ObjectId = ObjectId(),
    name: String = "",
    isGenderSeparated: Boolean = false,
    values: List<Value> = listOf(),
) {
    var name by mutableStateOf(name)
    var isGenderSeparated by mutableStateOf(isGenderSeparated)
    val values = values.toMutableStateList()

    class Value(
        val id: ObjectId = ObjectId(),
        name: String = "",
        isAdded: Boolean = true,
    ) {
        var name by mutableStateOf(name)
        var isAdded by mutableStateOf(isAdded)
    }
}

fun Discipline.toEditDiscipline() = EditDiscipline(
    id = id,
    name = name,
    isGenderSeparated = isGenderSeparated,
    values = values.map { value ->
        EditDiscipline.Value(
            id = value.id,
            name = value.name,
            isAdded = value.isAdded,
        )
    },
)
