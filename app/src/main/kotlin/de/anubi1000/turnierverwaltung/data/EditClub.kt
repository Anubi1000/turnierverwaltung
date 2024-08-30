package de.anubi1000.turnierverwaltung.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.util.Identifiable
import org.mongodb.kbson.ObjectId

@Stable
class EditClub(
    override val id: ObjectId = ObjectId(),
    name: String = "",
) : Identifiable {
    private var _name = mutableStateOf(name)

    private var isEditable = true

    val name: String by _name
    fun setName(name: String) {
        if (isEditable) _name.value = name
    }

    fun blockEditing() {
        isEditable = false
    }

    fun toClub(): Club {
        return Club().also {
            it.id = id
            it.name = name
        }
    }
}

fun Club.toEditClub() = EditClub(
    id = id,
    name = name
)