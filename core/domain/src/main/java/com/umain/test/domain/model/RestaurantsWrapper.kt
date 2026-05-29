package com.umain.test.domain.model

data class RestaurantsWrapper(
    val restaurants: List<Restaurant>,
    val allFilters: List<Filter>
)