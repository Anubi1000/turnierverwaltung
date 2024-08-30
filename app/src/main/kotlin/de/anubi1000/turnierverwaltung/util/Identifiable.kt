package de.anubi1000.turnierverwaltung.util

import org.mongodb.kbson.ObjectId

interface Identifiable {
    val id: ObjectId
}