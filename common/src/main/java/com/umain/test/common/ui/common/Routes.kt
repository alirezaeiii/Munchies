package com.umain.test.common.ui.common

sealed class Routes(val title: String) {
    object Restaurants : Routes("restaurants_screen")
    object Details : Routes("details_screen/{$RESTAURANT}")

    companion object Companion {
        const val RESTAURANT = "restaurant"
    }
}