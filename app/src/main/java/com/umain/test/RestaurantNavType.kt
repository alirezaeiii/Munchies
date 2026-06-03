package com.umain.test

import android.os.Build
import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.umain.test.domain.model.Restaurant

class RestaurantNavType : NavType<Restaurant>(isNullableAllowed = false) {

    private val gson by lazy { Gson() }

    override fun get(bundle: Bundle, key: String): Restaurant? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, Restaurant::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key)
        }
    }

    override fun parseValue(value: String): Restaurant {
        return try {
            gson.fromJson(value, Restaurant::class.java)
        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON format for Restaurant: $value", e)
        }
    }

    override fun put(bundle: Bundle, key: String, value: Restaurant) {
        bundle.putParcelable(key, value)
    }
}