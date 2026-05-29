package com.umain.test.feature.properties

import com.umain.test.common.base.BaseScreenState
import com.umain.test.common.base.ViewState
import com.umain.test.domain.model.Restaurant
import com.umain.test.domain.model.RestaurantsWrapper

data class RestaurantsViewState(
    override val base: ViewState<RestaurantsWrapper> = ViewState(),
    val filteredRestaurants: List<Restaurant> = emptyList(),
    val activeFilters: List<String> = emptyList()
) : BaseScreenState<RestaurantsWrapper> {

    override fun copyWithBase(base: ViewState<RestaurantsWrapper>) = copy(base = base)
}