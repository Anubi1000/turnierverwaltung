package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.database.queryById
import de.anubi1000.turnierverwaltung.util.tempRealm
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import org.mongodb.kbson.ObjectId

@Suppress("ktlint:standard:class-signature")
class ClubRepositoryImplTest : FunSpec({
    val realm = tempRealm()
    lateinit var repository: ClubRepositoryImpl

    beforeTest {
        repository = ClubRepositoryImpl(realm())
    }

    context("getAllForTournamentAsFlow") {
        test("sorts correctly") {
            val realm = realm()

            val clubs = (3 downTo 1).map {
                Club(name = it.toString())
            }

            val tournament = Tournament()

            realm.write {
                val dbTournament = copyToRealm(tournament)
                val dbClubs = clubs.map(::copyToRealm)

                dbTournament.clubs.addAll(dbClubs)
            }

            val dbClubs = repository.getAllForTournamentAsFlow(tournament.id).first()
            dbClubs shouldHaveSize clubs.size

            val sortedClubs = clubs.sortedWith(compareBy { it.name })

            sortedClubs.forEachIndexed { index, club ->
                dbClubs[index].name shouldBe club.name
            }
        }
    }

    context("insert") {
        test("should throw tournament not found exception") {
            val club = Club()

            val exception = shouldThrow<IllegalArgumentException> {
                repository.insert(club, ObjectId())
            }

            exception.message shouldBe "Tournament with with specified id not found"
        }
    }

    context("update") {
        test("updates correct properties") {
            val realm = realm()
            val club = Club(
                name = "Test 1",
            )

            realm.write {
                copyToRealm(club)
            }

            val changedClub = Club(
                id = club.id,
                name = "Test 2",
            )
            repository.update(changedClub)

            val dbClub = realm.queryById<Club>(club.id)
            dbClub shouldNotBeNull {
                name shouldBe changedClub.name
            }
        }

        test("should throw club not found exception") {
            val exception = shouldThrow<IllegalArgumentException> {
                repository.update(Club())
            }

            exception.message shouldBe "Club with with specified id not found"
        }
    }

    context("delete") {
        test("blocks delete if participants with club exist") {
            val realm = realm()

            val club = Club()

            realm.write {
                val dbClub = copyToRealm(club)
                copyToRealm(
                    Participant(
                        club = dbClub,
                    ),
                )
            }

            val exception = shouldThrow<IllegalArgumentException> {
                repository.delete(club.id)
            }

            exception.message shouldBe "Club with specified id is used by participants"
        }
    }
})
