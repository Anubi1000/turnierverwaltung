package de.anubi1000.turnierverwaltung.database.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Club() : RealmObject {
    // id
    @PrimaryKey
    @PersistedName("_id")
    var id: ObjectId = ObjectId()

    // name
    var name: String = ""
}