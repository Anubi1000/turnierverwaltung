package de.anubi1000.turnierverwaltung.util

import org.mongodb.kbson.ObjectId

fun String.toObjectId(): ObjectId = ObjectId(this)
