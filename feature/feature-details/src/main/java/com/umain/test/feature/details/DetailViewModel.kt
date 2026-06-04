package com.umain.test.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.umain.test.common.ui.common.Screens.Companion.RESTAURANT
import com.umain.test.domain.model.ErrorResponse
import com.umain.test.domain.model.Restaurant
import com.umain.test.domain.repository.StatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: StatusRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailViewState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun load() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        try {
            val status =
                repository.getStatus(savedStateHandle.get<Restaurant>(RESTAURANT)?.id!!)
            _state.value = DetailViewState(isOpen = status.isCurrentlyOpen)
        } catch (t: Throwable) {
            val errorState = if (t is HttpException && t.code() == 404) {
                val errorBody = t.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                val reason = errorResponse?.reason
                DetailViewState(error = t, errorMessage = reason)
            } else {
                DetailViewState(error = t)
            }
            _state.value = errorState
        }
    }
}