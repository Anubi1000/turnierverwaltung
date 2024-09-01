package de.anubi1000.turnierverwaltung.database.model

import de.anubi1000.turnierverwaltung.util.Identifiable
import io.realm.kotlin.ext.backlinks
import io.realm.kotlin.ext.realmDictionaryOf
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmDictionary
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

/**
 * Represents a participant in a [Tournament].
 */
class Participant() : RealmObject, Identifiable {
    /**
     * The id of the participant.
     */
    @PrimaryKey
    @PersistedName("_id")
    override var id: ObjectId = ObjectId()

    /**
     * The name of the participant.
     */
    var name: String = ""

    /**
     * The start number for the participant.
     */
    var startNumber: Int = 0

    /**
     * The [Gender] of the participant.
     */
    @Ignore
    var gender: Gender
        get() = if (_gender) Gender.FEMALE else Gender.MALE
        set(value) {
            _gender = value == Gender.FEMALE
        }

    /**
     * Backing field for [gender] which is compatible with realm.
     */
    @PersistedName("gender")
    private var _gender: Boolean = false

    /**
     * Represents the gender of a participant
     */
    enum class Gender {
        MALE, FEMALE
    }

    /**
     * The [Club] of the participant.
     */
    var club: Club? = null

    val tournament: RealmResults<Tournament> by backlinks(Tournament::participants)

    /**
     * The results for the individual disciplines of a participant.
     * The id of the [Discipline] is used as the key.
     */
    var results: RealmDictionary<DisciplineResult?> = realmDictionaryOf()

    /**
     * Results of a participant of a specific discipline.
     */
    class DisciplineResult() : EmbeddedRealmObject {
        /**
         * List containing the results of the individual rounds of the participant .
         */
        var rounds: RealmList<RoundResult> = realmListOf()
    }

    /**
     * Results of a participant for an individual round of a discipline.
     */
    class RoundResult() : EmbeddedRealmObject {
        /**
         * The individual values used for calculating the result for the [Participant].
         * The id of the [Discipline.Value] is used as the key.
         */
        var values: RealmDictionary<Double> = realmDictionaryOf()
    }
}