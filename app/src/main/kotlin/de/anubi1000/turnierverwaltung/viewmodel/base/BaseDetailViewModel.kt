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

abstract class BaseDetailViewModel<T : Identifiable, R>(private val repository: R) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    fun loadItem(id: ObjectId) {
        log.debug { "Loading item with id(${id.toHexString()})" }
        viewModelScope.launch {
            val item = repository.getItemById(id)!!
            state = State.Loaded(item)
        }
    }

    protected abstract suspend fun R.getItemById(id: ObjectId): T?

    fun deleteItem() {
        val currentState = state
        require(currentState is State.Loaded<*>) { "State needs to be loaded" }

        @Suppress("UNCHECKED_CAST")
        currentState as State.Loaded<T>

        log.debug { "Deleting item with id(${currentState.item.id.toHexString()})" }
        viewModelScope.launch {
            repository.deleteItem(currentState.item)
        }
    }

    protected abstract suspend fun R.deleteItem(item: T)

    companion object {
        private val log = logger()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded<T : Identifiable>(val item: T) : State
    }
}