package com.umain.test.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umain.test.common.ui.common.Screens.Companion.RESTAURANT
import com.umain.test.domain.model.Restaurant
import com.umain.test.domain.repository.StatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: StatusRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _status = MutableStateFlow(DetailViewState())
    val state = _status.asStateFlow()

    init {
        load()
    }

    fun load() = viewModelScope.launch {
        _status.update { it.copy(isLoading = true) }
        try {
            val status =
                repository.getStatus(savedStateHandle.get<Restaurant>(RESTAURANT)?.id!!)
            _status.value = DetailViewState(isOpen = status.isCurrentlyOpen)
        } catch (t: Throwable) {
            _status.value = DetailViewState(error = t)
        }
    }
}