package de.anubi1000.turnierverwaltung.database.model

import de.anubi1000.turnierverwaltung.util.Identifiable
import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Club() : RealmObject, Identifiable {
    // id
    @PrimaryKey
    @PersistedName("_id")
    override var id: ObjectId = ObjectId()

    // name
    var name: String = ""

    // tournament
    val tournament: RealmResults<Tournament> by backlinks(Tournament::clubs)
}