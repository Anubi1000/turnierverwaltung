package de.anubi1000.turnierverwaltung.database.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Discipline() : RealmObject {
    // id
    @PrimaryKey
    @PersistedName("_id")
    var id: ObjectId = ObjectId()

    // name
    var name: String = ""

    // isGenderSeparated
    var isGenderSeparated: Boolean = false

    // values
    var values: RealmList<Value> = realmListOf()
    @PersistedName("DisciplineValue")
    class Value() : EmbeddedRealmObject {
        var id: ObjectId = ObjectId()
        var name: String = ""
        var isAdded: Boolean = true
    }
}