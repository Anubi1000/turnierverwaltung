package de.anubi1000.turnierverwaltung.viewmodel.tounament

import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.database.model.Tournament
import de.anubi1000.turnierverwaltung.server.Server
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseDetailViewModel
import org.apache.logging.log4j.kotlin.logger
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId

@KoinViewModel
class TournamentDetailViewModel(
    repository: TournamentRepository,
    private val server: Server
) : BaseDetailViewModel<Tournament, TournamentRepository>(repository) {
    override suspend fun TournamentRepository.getItemById(id: ObjectId): Tournament? = getTournamentById(id)

    override suspend fun TournamentRepository.deleteItem(item: Tournament) {
        deleteTournament(item.id)
    }

    fun showTournamentOnScoreboard() {
        val currentState = state
        require(currentState is State.Loaded<*>) { "Tournament needs to be loaded" }

        @Suppress("UNCHECKED_CAST")
        currentState as State.Loaded<Tournament>

        log.debug("Changing tournament for scoreboard to current one")
        server.setCurrentTournament(currentState.item)
    }

    companion object {
        private val log = logger()
    }
}