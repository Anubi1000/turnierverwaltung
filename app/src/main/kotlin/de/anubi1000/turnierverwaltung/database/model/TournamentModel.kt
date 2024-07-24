package de.anubi1000.turnierverwaltung.database.model

import de.anubi1000.turnierverwaltung.util.toInstant
import de.anubi1000.turnierverwaltung.util.toRealmInstant
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.Instant

class TournamentModel() : RealmObject {
    // id
    @PrimaryKey
    @PersistedName("_id")
    var id: ObjectId = ObjectId()

    // name
    var name: String = ""

    // date
    @PersistedName("date")
    private var _date: RealmInstant = RealmInstant.now()
    var date: Instant
        get() = _date.toInstant()
        set(value) {
            _date = value.toRealmInstant()
        }

    // values
    var values: RealmList<Value> = realmListOf()
    class Value() : EmbeddedRealmObject {
        var id: ObjectId = ObjectId()
        var name: String = ""
        var subtract: Boolean = false
    }

    // participants
    var participants: RealmList<ParticipantModel> = realmListOf()
}