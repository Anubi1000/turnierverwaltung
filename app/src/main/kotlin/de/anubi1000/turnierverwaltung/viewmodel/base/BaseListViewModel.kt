package de.anubi1000.turnierverwaltung.viewmodel.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger

abstract class BaseListViewModel<T, R>(private val repository: R) : ViewModel() {
    var state: State by mutableStateOf(State.Loading)
        private set

    fun loadItems() {
        if (state is State.Loaded<*>) {
            log.debug("Skipping loading because items are already loaded")
            return
        } else {
            log.debug("Loading all items for list")
        }

        viewModelScope.launch {
            val flow = getFlowFromRepository(repository)
                .stateIn(scope = viewModelScope)
            state = State.Loaded(flow)
        }
    }

    protected abstract suspend fun getFlowFromRepository(repository: R): Flow<List<T>>

    companion object {
        private val log = logger()
    }

    sealed interface State {
        data object Loading : State
        data class Loaded<T>(val itemFlow: StateFlow<List<T>>) : State
    }
}