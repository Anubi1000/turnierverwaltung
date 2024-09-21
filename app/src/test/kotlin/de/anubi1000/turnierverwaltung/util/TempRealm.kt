package de.anubi1000.turnierverwaltung.util

import de.anubi1000.turnierverwaltung.database.createBaseRealmConfig
import io.kotest.core.TestConfiguration
import io.kotest.engine.spec.tempdir
import io.realm.kotlin.Realm

fun TestConfiguration.tempRealm(): () -> Realm {
    val dir = tempdir()
    lateinit var realm: Realm

    beforeTest {
        realm = Realm.open(
            createBaseRealmConfig()
                .directory(dir.path)
                .inMemory()
                .build(),
        )
    }

    afterTest {
        realm.close()
        // Run gc so realm file is closed otherwise the deletion of the temp dir fails
        System.gc()
    }

    return { realm }
}
