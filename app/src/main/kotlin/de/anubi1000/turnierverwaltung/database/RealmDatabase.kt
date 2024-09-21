package de.anubi1000.turnierverwaltung.database

import de.anubi1000.turnierverwaltung.database.model.Club
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.database.model.Participant
import de.anubi1000.turnierverwaltung.database.model.Team
import de.anubi1000.turnierverwaltung.database.model.TeamDiscipline
import de.anubi1000.turnierverwaltung.database.model.Tournament
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.jetbrains.annotations.VisibleForTesting
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

@VisibleForTesting
fun createBaseRealmConfig() = RealmConfiguration.Builder(
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

private fun createRealm(): Realm {
    val documentsPath = FileSystemView.getFileSystemView().defaultDirectory.path
    val dataDir = Path(documentsPath).resolve("Turnierverwaltung")
    val config = createBaseRealmConfig()
        .schemaVersion(0)
        .directory(dataDir.pathString)
        .name("data.realm")
        .build()
    return Realm.open(config)
}
