package de.anubi1000.turnierverwaltung.viewmodel.tounament

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.data.ScoreboardData
import de.anubi1000.turnierverwaltung.data.repository.TournamentRepository
import de.anubi1000.turnierverwaltung.data.toScoreboardData
import de.anubi1000.turnierverwaltung.util.toWordDocument
import io.github.vinceglb.filekit.core.FileKit
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId
import java.io.ByteArrayOutputStream

@KoinViewModel
class TournamentScoreboardViewModel(
    private val tournamentRepository: TournamentRepository,
) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)

    fun loadItem(id: ObjectId) {
        viewModelScope.launch {
            val tournament = tournamentRepository.getById(id)!!
            state = State.Loaded(tournament.toScoreboardData())
        }
    }

    fun saveTable(index: Int) {
        val currentState = state
        require(currentState is State.Loaded)

        viewModelScope.launch {
            currentState.data.toWordDocument(index).use { doc ->
                val bytes: ByteArray = ByteArrayOutputStream().use { stream ->
                    doc.write(stream)
                    stream.toByteArray()
                }

                FileKit.saveFile(
                    baseName = currentState.data.tables[index].name,
                    extension = "docx",
                    bytes = bytes,
                )
            }
        }
    }

    sealed interface State {
        data object Loading : State
        data class Loaded(val data: ScoreboardData) : State
    }
}
