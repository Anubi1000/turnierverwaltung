package de.anubi1000.turnierverwaltung.database

import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import de.anubi1000.turnierverwaltung.database.model.Tournament
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
        .Builder(
            schema = setOf(
                Club::class,
                Discipline::class,
                Discipline.Value::class,
                Participant::class,
                Participant.RoundResult::class,
                Participant.DisciplineResult::class,
                Team::class,
                TeamDiscipline::class,
                Tournament::class,
            ),
        )
        .schemaVersion(0)
        .deleteRealmIfMigrationNeeded()
        .directory(dataDir.pathString)
        .name("Data.realm")
        .build()
    return Realm.open(config)
}
