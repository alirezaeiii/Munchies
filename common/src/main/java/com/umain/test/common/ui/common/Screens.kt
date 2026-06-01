package com.umain.test.common.ui.common

sealed class Screens(val title: String) {
    object Restaurants : Screens("restaurants_screen")
    object Details : Screens("details_screen/{$RESTAURANT}")

    companion object {
        const val RESTAURANT = "restaurant"
    }
}