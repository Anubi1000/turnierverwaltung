package de.anubi1000.turnierverwaltung.ui.util

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelectable
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import de.anubi1000.turnierverwaltung.ui.util.screen.list.SelectableListItem
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@Suppress("ktlint:standard:class-signature")
@OptIn(ExperimentalTestApi::class)
class SelectableListItemTest : FunSpec({
    test("displays all properties") {
        runComposeUiTest {
            setContent {
                SelectableListItem(
                    headlineContent = { Text("Headline") },
                    supportingContent = { Text("Supporting") },
                    overlineContent = { Text("Overline") },
                    leadingContent = { Text("Leading") },
                    trailingContent = { Text("Trailing") },
                    selected = false,
                    onClick = {},
                )
            }

            arrayOf("Headline", "Supporting", "Overline", "Leading", "Trailing").forEach {
                onNodeWithText(it).assertIsDisplayed()
            }
        }
    }

    test("selected parameter is used") {
        runComposeUiTest {
            var selected by mutableStateOf(false)
            setContent {
                SelectableListItem(
                    headlineContent = { Text("Headline") },
                    selected = selected,
                    onClick = {},
                )
            }

            val item = onNodeWithText("Headline")
            item.assertIsSelectable()
                .assertIsNotSelected()

            selected = true

            item.assertIsSelected()
        }
    }

    test("onClick function is called") {
        runComposeUiTest {
            var called = false
            setContent {
                SelectableListItem(
                    headlineContent = { Text("Headline") },
                    selected = false,
                    onClick = {
                        called = true
                    },
                )
            }

            onNodeWithText("Headline").performClick()

            called shouldBe true
        }
    }
})
