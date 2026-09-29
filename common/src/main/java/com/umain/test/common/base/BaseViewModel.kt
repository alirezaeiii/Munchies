package com.umain.test.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umain.test.common.utils.Async
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<TYPE, STATE : BaseScreenState<TYPE, STATE>, QueryType, FetchType, EVENT: UiEvent>(
    private val repository: BaseRepository<TYPE, QueryType, FetchType>,
    private val createWarningEvent: (String) -> EVENT,
    initialState: STATE,
    private val queryValue: QueryType? = null,
    private val fetchValue: FetchType? = null,
    loadDataOnInit: Boolean = true
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<EVENT>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var job: Job? = null

    init {
        if (loadDataOnInit)
            refresh()
    }

    protected abstract fun onSuccess(items: TYPE)

    protected fun updateState(reducer: (STATE) -> STATE) {
        _state.update(reducer)
    }

    fun refresh(
        forceRefresh: Boolean = true
    ) {
        job?.cancel()
        job = repository.getResult(queryValue, fetchValue, forceRefresh).onEach { uiState ->
            when (uiState) {
                is Async.Loading -> {
                    updateState { old ->
                        reduceLoading(old, uiState.isRefreshing)
                    }
                }

                is Async.Success -> onSuccess(uiState.data)

                is Async.Error -> {
                    updateState { old ->
                        reduceError(old, uiState.message, uiState.isWarning)
                    }
                    if (uiState.isWarning) {
                        emitWarning(uiState.message)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun reduceLoading(old: STATE, isRefreshing: Boolean): STATE =
        old.withLoading(isRefreshing)


    private fun reduceError(old: STATE, msg: String, isWarning: Boolean): STATE =
        old.withError(msg, isWarning)

    private suspend fun emitWarning(message: String) {
        _uiEvent.emit(createWarningEvent(message))
    }

    protected fun emitEvent(event: EVENT) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}