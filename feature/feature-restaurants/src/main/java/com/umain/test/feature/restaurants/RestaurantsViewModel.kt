package com.umain.test.feature.restaurants

import android.net.Uri
import com.google.gson.Gson
import com.umain.test.common.base.BaseRepository
import com.umain.test.common.base.BaseViewModel
import com.umain.test.common.base.ViewState
import com.umain.test.common.ui.common.Routes
import com.umain.test.common.ui.common.Routes.Companion.RESTAURANT
import com.umain.test.domain.model.Restaurant
import com.umain.test.domain.model.RestaurantsWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    repository: BaseRepository<RestaurantsWrapper, Nothing, Nothing>
) : BaseViewModel<RestaurantsWrapper, RestaurantsViewState, Nothing, Nothing, RestaurantsUiEvent>(
    repository,
    RestaurantsUiEvent::ShowWarning,
    RestaurantsViewState(base = ViewState(isLoading = true))
) {

    override fun onSuccess(items: RestaurantsWrapper) {
        val activeFilters = state.value.activeFilters
        val filteredRestaurants = getFilteredRestaurants(activeFilters, items)

        updateState {
            RestaurantsViewState(
                base = ViewState(
                    items = items
                ),
                filteredRestaurants = filteredRestaurants,
                activeFilters = activeFilters
            )
        }
    }

    fun onFilterChanged(filters: List<String>) {
        val items = state.value.base.items ?: return
        val filteredRestaurants = getFilteredRestaurants(filters, items)

        updateState {
            it.copy(
                filteredRestaurants = filteredRestaurants,
                activeFilters = filters
            )
        }
    }

    fun onRestaurantClick(restaurant: Restaurant) {
        val json = Uri.encode(Gson().toJson(restaurant))
        val route = Routes.Details.title.replace("{${RESTAURANT}}", json)
        emitEvent(RestaurantsUiEvent.Navigate(route))
    }

    private fun getFilteredRestaurants(filters: List<String>, items: RestaurantsWrapper) =
        if (filters.isEmpty()) {
            items.restaurants
        } else {
            items.restaurants.filter { restaurant ->
                filters.all { selectedFilter ->
                    selectedFilter in restaurant.filterIds
                }
            }
        }
}
