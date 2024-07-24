package de.anubi1000.turnierverwaltung.data.tournament

import de.anubi1000.turnierverwaltung.database.model.TournamentModel
import io.realm.kotlin.ext.toRealmList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.mongodb.kbson.ObjectId
import java.time.Instant

data class Tournament(
    val id: ObjectId = ObjectId(),
    val name: String = "",
    val date: Instant = Instant.EPOCH,
    val values: PersistentList<Tournament.Value> = persistentListOf()
) {
    fun toTournamentModel(): TournamentModel {
        return TournamentModel().also { model ->
            model.id = id
            model.name = name
            model.date = date
            model.values = values.map { value ->
                TournamentModel.Value().also {
                    it.id = id
                    it.name = value.name
                    it.subtract = value.subtract
                }
            }.toRealmList()
        }
    }

    data class Value(
        val id: ObjectId = ObjectId(),
        val name: String = "",
        val subtract: Boolean = false
    )
}

fun TournamentModel.toTournament(): Tournament {
    return Tournament(
        id = this.id,
        name = this.name,
        date = this.date,
        values = this.values.map {
            Tournament.Value(it.id, it.name, it.subtract)
        }.toPersistentList()
    )
}
