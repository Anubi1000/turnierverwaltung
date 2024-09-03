package de.anubi1000.turnierverwaltung.viewmodel.discipline

import androidx.compose.runtime.derivedStateOf
import de.anubi1000.turnierverwaltung.data.EditDiscipline
import de.anubi1000.turnierverwaltung.data.repository.DisciplineRepository
import de.anubi1000.turnierverwaltung.database.model.Discipline
import de.anubi1000.turnierverwaltung.server.Server
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseEditViewModel
import io.realm.kotlin.ext.toRealmList
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class DisciplineEditViewModel(
    repository: DisciplineRepository,
    private val server: Server,
    @InjectedParam private val tournamentId: ObjectId
) : BaseEditViewModel<EditDiscipline, DisciplineRepository>(repository) {
    override fun getDefaultItem(): EditDiscipline = EditDiscipline()

    override suspend fun DisciplineRepository.updateItem(item: EditDiscipline) {
        TODO("Not yet implemented")
    }

    override suspend fun DisciplineRepository.insertItem(item: EditDiscipline) {
        insertDiscipline(
            discipline = Discipline().apply {
                id = item.id
                name = item.name
                isGenderSeparated = item.isGenderSeparated
                values = item.values.map { value -> Discipline.Value().apply {
                    id = value.id
                    name = value.name
                    isAdded = value.isAdded
                }}.toRealmList()
            },
            tournamentId = tournamentId
        )
        server.sendCurrentTournament()
    }


    override fun getValidationState(item: EditDiscipline): ComposeState<Boolean> {
        return derivedStateOf {
            item.name.isNotBlank() &&
                    item.values.isNotEmpty() &&
                    item.values.all { it.name.isNotEmpty() }
        }
    }

    override suspend fun DisciplineRepository.getItemById(id: ObjectId): EditDiscipline? {
        TODO("Not yet implemented")
    }

}