package com.umain.test.feature.restaurants

import com.umain.test.common.base.UiEvent

sealed interface RestaurantsUiEvent : UiEvent {
    data class ShowWarning(override val message: String) : RestaurantsUiEvent, UiEvent.Warning
    data class Navigate(override val route: String) : RestaurantsUiEvent, UiEvent.Navigation
}