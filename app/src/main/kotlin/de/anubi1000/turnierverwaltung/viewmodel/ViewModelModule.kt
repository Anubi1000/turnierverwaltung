package de.anubi1000.turnierverwaltung.viewmodel

import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentDetailViewModel
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentEditViewModel
import de.anubi1000.turnierverwaltung.viewmodel.tounament.TournamentListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::TournamentListViewModel)
    viewModelOf(::TournamentDetailViewModel)
    viewModelOf(::TournamentEditViewModel)

    viewModelOf(::ParticipantListViewModel)
    viewModelOf(::TeamListViewModel)
}