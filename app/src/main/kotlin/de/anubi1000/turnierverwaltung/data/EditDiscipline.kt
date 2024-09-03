package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import de.anubi1000.turnierverwaltung.util.Identifiable
import org.mongodb.kbson.ObjectId

class EditDiscipline(
    override val id: ObjectId = ObjectId(),
    name: String = "",
    isGenderSeparated: Boolean = false,
    values: List<Value> = listOf()
) : Identifiable {
    var name by mutableStateOf(name)
    var isGenderSeparated by mutableStateOf(isGenderSeparated)
    val values = values.toMutableStateList()

    class Value(
        val id: ObjectId = ObjectId(),
        name: String = "",
        isAdded: Boolean = true
    ) {
        var name by mutableStateOf(name)
        var isAdded by mutableStateOf(isAdded)
    }
}