package com.umain.test.common.utils

import android.os.Build
import android.os.Bundle

fun <T> Bundle.asRestaurant(key: String, clazz: Class<T>) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key)
    }