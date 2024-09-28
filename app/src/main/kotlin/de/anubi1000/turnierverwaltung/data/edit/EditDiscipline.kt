package de.anubi1000.turnierverwaltung.data.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import de.anubi1000.turnierverwaltung.data.validation.validateAmountOfBestRoundsToShow
import de.anubi1000.turnierverwaltung.data.validation.validateName
import de.anubi1000.turnierverwaltung.database.model.Discipline
import io.realm.kotlin.ext.toRealmList
import org.mongodb.kbson.ObjectId

class EditDiscipline(
    val id: ObjectId = ObjectId(),
    name: String = "",
    isGenderSeparated: Boolean = false,
    values: List<Value> = listOf(),
    amountOfBestRoundsToShow: String = "1",
) {
    var name by mutableStateOf(name)
    var isGenderSeparated by mutableStateOf(isGenderSeparated)
    val values = values.toMutableStateList()
    var amountOfBestRoundsToShow by mutableStateOf(amountOfBestRoundsToShow)

    class Value(
        val id: ObjectId = ObjectId(),
        name: String = "",
        isAdded: Boolean = true,
    ) {
        var name by mutableStateOf(name)
        var isAdded by mutableStateOf(isAdded)
    }

    fun toDiscipline() = Discipline(
        id = id,
        name = validateName(name)!!,
        isGenderSeparated = isGenderSeparated,
        values = values.map { value ->
            Discipline.Value().also {
                it.id = value.id
                it.name = validateName(value.name)!!
                it.isAdded = value.isAdded
            }
        }.toRealmList(),
        amountOfBestRoundsToShow = validateAmountOfBestRoundsToShow(amountOfBestRoundsToShow)!!,
    )
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
    amountOfBestRoundsToShow = amountOfBestRoundsToShow.toString(),
)
