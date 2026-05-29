package com.umain.test.feature.properties

import com.umain.test.common.base.BaseRepository
import com.umain.test.common.base.BaseViewModel
import com.umain.test.common.base.ViewState
import com.umain.test.domain.model.RestaurantsWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    repository: BaseRepository<RestaurantsWrapper, Nothing, Nothing>
) : BaseViewModel<RestaurantsWrapper, RestaurantsViewState, Nothing, Nothing>(
    repository,
    RestaurantsViewState(base = ViewState(isLoading = true))
) {

    override fun onSuccess(items: RestaurantsWrapper, isUserRefresh: Boolean) {
        if (isUserRefresh) {
            submitQuery(_state.value.activeFilters, items)
        } else {
            _state.value = RestaurantsViewState(
                base = ViewState(
                    items = items,
                ),
                filteredRestaurants = items.restaurants
            )
        }
    }

    fun onFilterChanged(filters: List<String>) {
        updateState { old ->
            old.copy(activeFilters = filters)
        }
        refresh(
            isUserRefresh = true,
            forceRefresh = false
        )
    }

    private fun submitQuery(activeFilters: List<String>, restaurantItems: RestaurantsWrapper) {
        val filteredRestaurants =
            if (activeFilters.isEmpty()) {
                restaurantItems.restaurants
            } else {
                restaurantItems.restaurants.filter { restaurant ->
                    activeFilters.all { selectedFilter ->
                        selectedFilter in restaurant.filterIds
                    }
                }
            }

        _state.value = RestaurantsViewState(
            base = ViewState(
                items = restaurantItems
            ),
            filteredRestaurants = filteredRestaurants,
            activeFilters = activeFilters
        )
    }
}
