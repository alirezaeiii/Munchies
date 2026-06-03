package com.umain.test.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Restaurant(
    val id: String,
    val name: String,
    val rating: Float,
    val filterIds: List<String>,
    val filterNames: List<String> = emptyList(),
    val imageUrl: String,
    val deliveryTimeMinutes: Int
) : Parcelable