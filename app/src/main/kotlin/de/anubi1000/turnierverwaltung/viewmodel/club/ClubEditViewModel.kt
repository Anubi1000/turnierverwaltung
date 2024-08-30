package de.anubi1000.turnierverwaltung.viewmodel.club

import androidx.compose.runtime.derivedStateOf
import de.anubi1000.turnierverwaltung.data.EditClub
import de.anubi1000.turnierverwaltung.data.repository.ClubRepository
import de.anubi1000.turnierverwaltung.data.toEditClub
import de.anubi1000.turnierverwaltung.viewmodel.base.BaseEditViewModel
import org.koin.android.annotation.KoinViewModel
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

@KoinViewModel
class ClubEditViewModel(repository: ClubRepository) : BaseEditViewModel<EditClub, ClubRepository>(repository) {
    override fun getDefaultItem(): EditClub = EditClub()

    override suspend fun ClubRepository.getItemById(id: ObjectId): EditClub? = getClubById(id)?.toEditClub()

    override fun getValidationState(item: EditClub): ComposeState<Boolean> {
        return derivedStateOf {
            item.name.isNotBlank()
        }
    }

    override suspend fun ClubRepository.insertItem(item: EditClub) {
        insertClub(item.toClub())
    }

    override suspend fun ClubRepository.updateItem(item: EditClub) {
        updateClub(item.toClub())
    }
}