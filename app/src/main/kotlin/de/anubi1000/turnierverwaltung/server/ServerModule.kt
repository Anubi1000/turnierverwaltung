package de.anubi1000.turnierverwaltung.server

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val serverModule = module {
    singleOf(::ServerViewModel)
}