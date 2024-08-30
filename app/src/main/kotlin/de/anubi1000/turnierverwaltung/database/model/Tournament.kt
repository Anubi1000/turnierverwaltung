package de.anubi1000.turnierverwaltung.database.model

import de.anubi1000.turnierverwaltung.util.Identifiable
import de.anubi1000.turnierverwaltung.util.toInstant
import de.anubi1000.turnierverwaltung.util.toRealmInstant
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.Instant

/**
 * Represents a tournament.
 */
class Tournament() : RealmObject, Identifiable {
    /**
     * The id of the tournament.
     */
    @PrimaryKey
    @PersistedName("_id")
    override var id: ObjectId = ObjectId()

    /**
     * The name of the tournament.
     */
    var name: String = ""

    /**
     * The date that the tournament is scheduled on.
     */
    @Ignore
    var date: Instant
        get() = _date.toInstant()
        set(value) {
            _date = value.toRealmInstant()
        }

    /**
     * Backing field for [date] which is compatible with realm.
     */
    @PersistedName("date")
    private var _date: RealmInstant = RealmInstant.now()

    /**
     * The list of clubs participating in the tournament.
     */
    var clubs: RealmList<Club> = realmListOf()

    /**
     * The list of participants participating in the tournament.
     */
    var participants: RealmList<Participant> = realmListOf()

    /**
     * The list of teams participating in the tournament.
     */
    var teams: RealmList<Team> = realmListOf()

    /**
     * The list of disciplines that are in the tournament.
     */
    var disciplines: RealmList<Discipline> = realmListOf()

    /**
     * The list of team disciplines that are in the tournament.
     */
    var teamDisciplines: RealmList<Team> = realmListOf()
}