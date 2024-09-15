package de.anubi1000.turnierverwaltung.data.validation

fun validateInt(string: String, min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Int? {
    val int = string.toIntOrNull() ?: return null
    return if (int in min..max) int else null
}

fun validateDouble(string: String): Double? {
    val replacedString = string.replace(',', '.')
    val double = replacedString.toDoubleOrNull()
    return double
}
