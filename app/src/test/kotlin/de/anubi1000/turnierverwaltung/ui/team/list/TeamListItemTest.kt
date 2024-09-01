package de.anubi1000.turnierverwaltung.ui.team.list

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.anubi1000.turnierverwaltung.database.model.Team
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val team = Team().apply {
    name = "Test"
}

@OptIn(ExperimentalTestApi::class)
class TeamListItemTest : FunSpec({
    test("Shows properties") {
        runComposeUiTest {
            setContent {
                TeamListItem(
                    team = team
                )
            }

            onNodeWithText(team.name).assertIsDisplayed()
        }
    }

    test("Is selected") {
        runComposeUiTest {
            setContent {
                TeamListItem(
                    team = team,
                    selected = true
                )
            }

            onNodeWithText(team.name)
                .assertIsSelected()
        }
    }

    test("Is not selected") {
        runComposeUiTest {
            setContent {
                TeamListItem(
                    team = team,
                    selected = false
                )
            }

            onNodeWithText(team.name)
                .assertIsNotSelected()
        }
    }

    test("Calls click actions") {
        runComposeUiTest {
            var clicked = false

            setContent {
                TeamListItem(
                    team = team,
                    onClick = { clicked = true }
                )
            }

            onNodeWithText(team.name)
                .performClick()

            clicked shouldBe true
        }
    }
})