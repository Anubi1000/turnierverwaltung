package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.util.tempRealm
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

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
})
