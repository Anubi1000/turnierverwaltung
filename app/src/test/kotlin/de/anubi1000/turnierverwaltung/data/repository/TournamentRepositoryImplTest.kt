package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.database.queryById
import de.anubi1000.turnierverwaltung.util.tempRealm
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.Instant

@Suppress("ktlint:standard:class-signature")
class TournamentRepositoryImplTest : FunSpec({
    val realm = tempRealm()
    lateinit var repository: TournamentRepositoryImpl

    beforeTest {
        repository = TournamentRepositoryImpl(realm())
    }

    context("getAllAsFlow") {
        test("sorts correctly") {
            val realm = realm()
            val tournaments = mutableListOf<Tournament>()

            for (x in 1..3) {
                for (y in 1..3) {
                    tournaments.add(
                        Tournament(
                            name = x.toString(),
                            date = Instant.EPOCH + Duration.ofSeconds(y.toLong()),
                        ),
                    )
                }
            }

            realm.write {
                tournaments.forEach(::copyToRealm)
            }

            val dbTournaments = repository.getAllAsFlow().first()
            dbTournaments shouldHaveSize tournaments.size

            tournaments.sortWith { t1, t2 ->
                val comp = t1.date.compareTo(t2.date)
                if (comp != 0) return@sortWith -comp
                t1.name.compareTo(t2.name)
            }

            tournaments.forEachIndexed { index, tournament ->
                dbTournaments[index].id shouldBe tournament.id
            }
        }
    }

    context("update") {
        test("updates a tournament") {
            val realm = realm()
            val tournament = Tournament(
                name = "Test 1",
                date = Instant.EPOCH,
                teamSize = 3,
            )

            realm.write {
                copyToRealm(tournament)
            }

            val changedTournament = Tournament(
                id = tournament.id,
                name = "Test 2",
                date = tournament.date + Duration.ofSeconds(10),
                teamSize = 1,
            )

            repository.update(changedTournament)

            val dbTournament = realm.queryById<Tournament>(tournament.id)
            dbTournament shouldNotBeNull {
                name shouldBe changedTournament.name
                date shouldBe changedTournament.date
                teamSize shouldBe tournament.teamSize
            }
        }

        test("should throw tournament not found exception") {
            val exception = shouldThrow<IllegalArgumentException> {
                repository.update(Tournament())
            }

            exception.message shouldStartWith "Tournament with specified id"
        }
    }
})
