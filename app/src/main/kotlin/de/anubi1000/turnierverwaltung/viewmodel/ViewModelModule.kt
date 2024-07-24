package de.anubi1000.turnierverwaltung.viewmodel

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::TournamentListViewModel)
    viewModelOf(::TournamentDetailViewModel)
    viewModelOf(::TournamentEditViewModel)
}