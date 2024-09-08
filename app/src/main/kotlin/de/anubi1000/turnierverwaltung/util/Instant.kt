package de.anubi1000.turnierverwaltung.util

import io.realm.kotlin.types.RealmInstant
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun RealmInstant.toInstant(): Instant = if (epochSeconds >= 0) {
    Instant.ofEpochSecond(epochSeconds, nanosecondsOfSecond.toLong())
} else {
    Instant.ofEpochSecond(epochSeconds - 1, 1_000_000 - nanosecondsOfSecond.toLong())
}

fun Instant.toRealmInstant(): RealmInstant = if (epochSecond >= 0) {
    RealmInstant.from(epochSecond, nano)
} else {
    RealmInstant.from(epochSecond + 1, -1_000_000 + nano)
}

fun Instant.formatAsDate(): String {
    val dateTime = OffsetDateTime.ofInstant(this, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return dateTime.format(formatter)
}
