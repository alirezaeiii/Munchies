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
            submitQuery(_state.value.filters, items)
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
            old.copy(filters = filters)
        }
        refresh(
            isUserRefresh = true,
            forceRefresh = false
        )
    }

    private fun submitQuery(filters: List<String>, restaurantItems: RestaurantsWrapper) {
        val filteredRestaurants =
            if (filters.isEmpty()) {
                restaurantItems.restaurants
            } else {
                restaurantItems.restaurants.filter { restaurant ->
                    filters.all { selectedFilter ->
                        selectedFilter in restaurant.filterIds
                    }
                }
            }

        _state.value = RestaurantsViewState(
            base = ViewState(
                items = restaurantItems
            ),
            filteredRestaurants = filteredRestaurants,
            filters = filters
        )
    }
}
