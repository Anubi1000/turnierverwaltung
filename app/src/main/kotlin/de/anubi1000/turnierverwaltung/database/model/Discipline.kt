package de.anubi1000.turnierverwaltung.database.model

import de.anubi1000.turnierverwaltung.util.Identifiable
import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Discipline() : RealmObject, Identifiable {
    // id
    @PrimaryKey
    @PersistedName("_id")
    override var id: ObjectId = ObjectId()

    // name
    var name: String = ""

    // isGenderSeparated
    var isGenderSeparated: Boolean = false

    val tournament: RealmResults<Tournament> by backlinks(Tournament::disciplines)

    // values
    var values: RealmList<Value> = realmListOf()
    @PersistedName("DisciplineValue")
    class Value() : EmbeddedRealmObject {
        var id: ObjectId = ObjectId()
        var name: String = ""
        var isAdded: Boolean = true
    }
}