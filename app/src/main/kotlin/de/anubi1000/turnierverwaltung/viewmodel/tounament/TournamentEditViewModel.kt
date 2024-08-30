package de.anubi1000.turnierverwaltung.viewmodel.tounament

import androidx.compose.runtime.derivedStateOf
import de.anubi1000.turnierverwaltung.data.EditTournament
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.data.toEditTournament
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseEditViewModel
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class TournamentEditViewModel(repository: TournamentRepository) : BaseEditViewModel<EditTournament, TournamentRepository>(repository) {
    override fun getDefaultItem(): EditTournament = EditTournament()

    override suspend fun TournamentRepository.getItemById(id: ObjectId): EditTournament? = getTournamentById(id)?.toEditTournament()

    override fun getValidationState(item: EditTournament): ComposeState<Boolean> {
        return derivedStateOf {
            item.name.isNotBlank()
        }
    }

    override suspend fun TournamentRepository.insertItem(item: EditTournament) {
        insertTournament(item.toTournament())
    }

    override suspend fun TournamentRepository.updateItem(item: EditTournament) {
        updateTournament(item.toTournament())
    }
}