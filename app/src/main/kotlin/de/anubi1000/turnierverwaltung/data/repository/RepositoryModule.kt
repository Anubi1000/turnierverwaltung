package de.anubi1000.turnierverwaltung.data.repository

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val repositoryModule = module {
    factoryOf(::TournamentRepositoryImpl) withOptions {
        bind<TournamentRepository>()
    }
}