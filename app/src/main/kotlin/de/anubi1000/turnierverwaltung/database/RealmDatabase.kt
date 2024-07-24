package de.anubi1000.turnierverwaltung.database

import de.anubi1000.turnierverwaltung.database.model.ParticipantModel
import de.anubi1000.turnierverwaltung.database.model.TournamentModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import javax.swing.filechooser.FileSystemView
import kotlin.io.path.Path
import kotlin.io.path.pathString

val databaseModule = module {
    single { createRealm() } withOptions {
        createdAtStart()
    }
}

private fun createRealm(): Realm {
    val documentsPath = FileSystemView.getFileSystemView().defaultDirectory.path
    val dataDir = Path(documentsPath).resolve("Turnierverwaltung")
    val config = RealmConfiguration
        .Builder(schema = setOf(
            TournamentModel::class,
            TournamentModel.Value::class,
            ParticipantModel::class,
            ParticipantModel.Result::class,
        ))
        .schemaVersion(0)
        .deleteRealmIfMigrationNeeded()
        .directory(dataDir.pathString)
        .name("Data.realm")
        .build()
    return Realm.open(config)
}