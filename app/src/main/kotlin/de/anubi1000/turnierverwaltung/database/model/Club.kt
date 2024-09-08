package de.anubi1000.turnierverwaltung.database.model

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Club(
    @PrimaryKey
    @PersistedName("_id")
    var id: ObjectId = ObjectId(),
    var name: String = "",
) : RealmObject {
    constructor() : this(id = ObjectId())

    val tournament: RealmResults<Tournament> by backlinks(Tournament::clubs)
}
