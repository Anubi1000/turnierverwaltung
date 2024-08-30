package de.anubi1000.turnierverwaltung.viewmodel.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.anubi1000.turnierverwaltung.util.Identifiable
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import org.mongodb.kbson.ObjectId
import androidx.compose.runtime.State as ComposeState

abstract class BaseEditViewModel<T : Identifiable, R>(private val repository: R) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    private var isEditMode = false

    fun loadCreate() {
        log.debug("Loading item creation")
        isEditMode = false

        val item = getDefaultItem()
        state = State.Loaded(item, getValidationState(item))
    }

    protected abstract fun getDefaultItem(): T

    fun loadEdit(id: ObjectId) {
        log.debug { "Loading item editing for id(${id.toHexString()})" }
        isEditMode = true

        viewModelScope.launch {
            val item = repository.getItemById(id)!!
            state = State.Loaded(item, getValidationState(item))
        }
    }

    protected abstract suspend fun R.getItemById(id: ObjectId): T?

    protected abstract fun getValidationState(item: T): ComposeState<Boolean>

    fun saveChanges(onSaved: (ObjectId) -> Unit) {
        val currentState = state
        require(
            currentState is State.Loaded<*>
        ) { "State needs to be loaded" }

        // TODO: Edit blocking

        require(currentState.isValid.value) { "Club is not valid" }

        log.debug {
            if (isEditMode) {
                "Saving changes for edit with id(${currentState.item.id.toHexString()})"
            } else {
                "Saving changes for creation"
            }
        }


        viewModelScope.launch {
            @Suppress("UNCHECKED_CAST")
            if (isEditMode) {
                repository.updateItem(currentState.item as T)
            } else {
                repository.insertItem(currentState.item as T)
            }
            onSaved(currentState.item.id)
        }
    }

    protected abstract suspend fun R.insertItem(item: T)
    protected abstract suspend fun R.updateItem(item: T)

    companion object {
        private val log = logger()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded<T : Identifiable>(
            val item: T,
            val isValid: ComposeState<Boolean>
        ) : State
    }
}