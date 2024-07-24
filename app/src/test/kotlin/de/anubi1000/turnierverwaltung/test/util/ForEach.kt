package de.anubi1000.turnierverwaltung.test.util

inline fun forEachBoolean(action: (Boolean) -> Unit) {
    action(true)
    action(false)
}