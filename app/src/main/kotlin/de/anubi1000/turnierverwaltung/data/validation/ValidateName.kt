package de.anubi1000.turnierverwaltung.data.validation

fun validateName(name: String): String? = if (name.isNotBlank()) {
    name.replace("\n", "").trim()
} else {
    null
}
