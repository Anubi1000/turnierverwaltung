package de.anubi1000.turnierverwaltung.data.validation

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

@Suppress("ktlint:standard:class-signature")
class ValidateNameTest : FunSpec({
    test("test valid names") {
        val items = arrayOf(
            "Test",
            "  Test",
            "Test  ",
            "  Test  ",
            "\n  Test",
            "Test\n  \n  ",
        )

        items.forEach { item ->
            withClue("\"$item\" should equal Test") {
                validateName(item) shouldBe "Test"
            }
        }
    }

    test("test invalid names") {
        val items = arrayOf(
            "",
            "    ",
            "\n  ",
            "  \n",
            "\n\n",
        )

        items.forEach { item ->
            withClue("\"$item\" should not be valid") {
                validateName(item).shouldBeNull()
            }
        }
    }
})
