package com.umain.test.feature.restaurants

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

    override fun onSuccess(items: RestaurantsWrapper) {
        val activeFilters = _state.value.activeFilters
        val filteredRestaurants =
            if (activeFilters.isEmpty()) {
                items.restaurants
            } else {
                items.restaurants.filter { restaurant ->
                    activeFilters.all { selectedFilter ->
                        selectedFilter in restaurant.filterIds
                    }
                }
            }

        _state.value = RestaurantsViewState(
            base = ViewState(
                items = items
            ),
            filteredRestaurants = filteredRestaurants,
            activeFilters = activeFilters
        )
    }

    fun onFilterChanged(filters: List<String>) {
        updateState { old ->
            old.copy(activeFilters = filters)
        }
        refresh(forceRefresh = false)
    }
}
