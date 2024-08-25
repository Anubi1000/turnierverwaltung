package de.anubi1000.turnierverwaltung.database.model

import io.realm.kotlin.ext.realmDictionaryOf
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmDictionary
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Team() : RealmObject {
    // id
    @PrimaryKey
    @PersistedName("_id")
    var id: ObjectId = ObjectId()

    // name
    var name: String = ""

    // startNumber
    var startNumber: Int = 0

    // members
    var members: RealmList<Participant> = realmListOf()

    // participatingDisciplines (key: id of TeamDiscipline)
    var participatingDisciplines: RealmDictionary<Boolean> = realmDictionaryOf()
}