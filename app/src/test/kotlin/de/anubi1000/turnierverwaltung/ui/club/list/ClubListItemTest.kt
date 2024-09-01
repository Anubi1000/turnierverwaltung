package de.anubi1000.turnierverwaltung.ui.club.list

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.anubi1000.turnierverwaltung.database.model.Club
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val club = Club().apply {
    name = "Test"
}

@OptIn(ExperimentalTestApi::class)
class ClubListItemTest : FunSpec({
    test("Shows properties") {
        runComposeUiTest {
            setContent {
                ClubListItem(
                    club = club
                )
            }

            onNodeWithText(club.name).assertIsDisplayed()
        }
    }

    test("Is selected") {
        runComposeUiTest {
            setContent {
                ClubListItem(
                    club = club,
                    selected = true
                )
            }

            onNodeWithText(club.name)
                .assertIsSelected()
        }
    }

    test("Is not selected") {
        runComposeUiTest {
            setContent {
                ClubListItem(
                    club = club,
                    selected = false
                )
            }

            onNodeWithText(club.name)
                .assertIsNotSelected()
        }
    }

    test("Calls click actions") {
        runComposeUiTest {
            var clicked = false

            setContent {
                ClubListItem(
                    club = club,
                    onClick = { clicked = true }
                )
            }

            onNodeWithText(club.name)
                .performClick()

            clicked shouldBe true
        }
    }
})