package de.anubi1000.turnierverwaltung.database.model

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Discipline(
    @PrimaryKey
    @PersistedName("_id")
    var id: ObjectId = ObjectId(),
    var name: String = "",
    var isGenderSeparated: Boolean = false,
    var values: RealmList<Value> = realmListOf(),
) : RealmObject {
    constructor() : this(id = ObjectId())

    val tournament: RealmResults<Tournament> by backlinks(Tournament::disciplines)

    @PersistedName("DisciplineValue")
    class Value : EmbeddedRealmObject {
        var id: ObjectId = ObjectId()
        var name: String = ""
        var isAdded: Boolean = true
    }
}
