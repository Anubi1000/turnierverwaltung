package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.database.queryById
import de.anubi1000.turnierverwaltung.util.tempRealm
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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

            val tournaments = (1..3).flatMap { x ->
                (1..3).map { y ->
                    Tournament(name = x.toString(), date = Instant.EPOCH + Duration.ofSeconds(y.toLong()))
                }
            }

            realm.write {
                tournaments.forEach(::copyToRealm)
            }

            val dbTournaments = repository.getAllAsFlow().first()
            dbTournaments shouldHaveSize tournaments.size

            val sortedTournaments = tournaments.sortedWith(compareByDescending<Tournament> { it.date }.thenBy { it.name })

            sortedTournaments.forEachIndexed { index, tournament ->
                dbTournaments[index].id shouldBe tournament.id
            }
        }
    }

    context("update") {
        test("updates correct properties") {
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
                teamSize shouldBe tournament.teamSize // Original team size should remain
            }
        }

        test("should throw tournament not found exception") {
            val exception = shouldThrow<IllegalArgumentException> {
                repository.update(Tournament())
            }

            exception.message shouldBe "Tournament with specified id not found"
        }
    }

    context("delete") {
        test("deletes everything of tournament") {
            val realm = realm()

            val tournament = Tournament()
            realm.write {
                copyToRealm(tournament).apply {
                    clubs.add(copyToRealm(Club()))
                    participants.add(copyToRealm(Participant()))
                    teams.add(copyToRealm(Team()))
                    disciplines.add(copyToRealm(Discipline()))
                    teamDisciplines.add(copyToRealm(TeamDiscipline()))
                }
            }

            suspend fun checkEntityCounts(expectedCount: Long) {
                val entities = listOf(
                    Tournament::class,
                    Club::class,
                    Participant::class,
                    Team::class,
                    Discipline::class,
                    TeamDiscipline::class,
                )
                for (entity in entities) {
                    realm.query(entity).count().asFlow().first() shouldBe expectedCount
                }
            }

            checkEntityCounts(1)

            repository.delete(tournament.id)

            checkEntityCounts(0)
        }
    }
})
