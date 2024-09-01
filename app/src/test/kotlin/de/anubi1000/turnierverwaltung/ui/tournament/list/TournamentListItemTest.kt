package de.anubi1000.turnierverwaltung.ui.tournament.list

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.util.formatAsDate
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val tournament = Tournament().apply {
    name = "Test"
}

@OptIn(ExperimentalTestApi::class)
class TournamentListItemTest : FunSpec({
    test("Shows properties") {
        runComposeUiTest {
            setContent {
                TournamentListItem(
                    tournament = tournament
                )
            }

            onNodeWithText(tournament.name).assertIsDisplayed()
            onNodeWithText(tournament.date.formatAsDate()).assertIsDisplayed()
        }
    }

    test("Is selected") {
        runComposeUiTest {
            setContent {
                TournamentListItem(
                    tournament = tournament,
                    selected = true
                )
            }

            onNodeWithText(tournament.name)
                .assertIsSelected()
        }
    }

    test("Is not selected") {
        runComposeUiTest {
            setContent {
                TournamentListItem(
                    tournament = tournament,
                    selected = false
                )
            }

            onNodeWithText(tournament.name)
                .assertIsNotSelected()
        }
    }

    test("Calls click actions") {
        runComposeUiTest {
            var clicked = false

            setContent {
                TournamentListItem(
                    tournament = tournament,
                    onClick = { clicked = true }
                )
            }

            onNodeWithText(tournament.name)
                .performClick()

            clicked shouldBe true
        }
    }
})