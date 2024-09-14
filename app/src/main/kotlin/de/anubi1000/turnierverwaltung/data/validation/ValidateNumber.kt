package de.anubi1000.turnierverwaltung.data.validation

fun validateInt(string: String): Int? {
    val int = string.toIntOrNull() ?: return null
    return if (int >= 0) int else null
}

fun validateDouble(string: String): Double? {
    val replacedString = string.replace(',', '.')
    val double = replacedString.toDoubleOrNull()
    return double
}
