package de.anubi1000.turnierverwaltung.database

import io.realm.kotlin.TypedRealm
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.TypedRealmObject

inline fun <reified T : TypedRealmObject> TypedRealm.getById(id: Any) = query<T>("_id == $0", id).first().find()