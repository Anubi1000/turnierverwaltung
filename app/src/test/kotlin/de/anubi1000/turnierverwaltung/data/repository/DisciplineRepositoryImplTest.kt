package de.anubi1000.turnierverwaltung.data.repository

import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.util.tempRealm
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.flow.first

@Suppress("ktlint:standard:class-signature")
class DisciplineRepositoryImplTest : FunSpec({
    val realm = tempRealm()
    lateinit var repository: DisciplineRepositoryImpl

    beforeTest {
        repository = DisciplineRepositoryImpl(realm())
    }

    context("getAllForTournamentAsFlow") {
        test("sorts correctly") {
            val realm = realm()

            var disciplines = (3 downTo 1).map {
                Discipline(
                    name = it.toString(),
                    isGenderSeparated = false,
                    values = realmListOf(
                        Discipline.Value().apply {
                            name = it.toString()
                        },
                    ),
                )
            }

            val tournament = Tournament()

            realm.write {
                val dbTournament = copyToRealm(tournament)
                val dbDisciplines = disciplines.map(::copyToRealm)

                dbTournament.disciplines.addAll(dbDisciplines)
            }

            val dbDisciplines = repository.getAllForTournamentAsFlow(tournament.id).first()
            dbDisciplines shouldHaveSize disciplines.size

            val sortedDisciplines = disciplines.sortedWith(compareBy { it.name })

            sortedDisciplines.forEachIndexed { index, discipline ->
                dbDisciplines[index].name shouldBe discipline.name
            }
        }
    }
})
