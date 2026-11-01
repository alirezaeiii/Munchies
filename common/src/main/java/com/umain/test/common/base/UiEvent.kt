package com.umain.test.common.base

sealed interface UiEvent {
    data class ShowWarning(val message: String) : UiEvent
    data class Navigate(val route: String) : UiEvent
    data object NavigateUp : UiEvent
}