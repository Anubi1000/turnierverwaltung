package de.anubi1000.turnierverwaltung.database.model

import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.ext.realmDictionaryOf
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmDictionary
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class ParticipantModel() : RealmObject {
    // id
    @PrimaryKey
    @PersistedName("_id")
    var id: ObjectId = ObjectId()

    // name
    var name: String = ""

    // verein
    var verein: String = ""

    // tournament
    val tournament: RealmResults<TournamentModel> by backlinks(TournamentModel::participants)

    // results
    var results: RealmList<Result> = realmListOf()
    class Result() : EmbeddedRealmObject {
        var values: RealmDictionary<Double> = realmDictionaryOf()
    }
}