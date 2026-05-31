package com.umain.test.data.response

import com.google.gson.annotations.SerializedName
import com.umain.test.domain.model.Restaurant

data class RestaurantResponse(
    val id: String,
    val name: String,
    val rating: Float,
    val filterIds: List<String>,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("delivery_time_minutes")
    val deliveryTimeMinutes: Int
)

fun List<RestaurantResponse>.asDomainModel() = map(RestaurantResponse::asDomainModel)

private fun RestaurantResponse.asDomainModel() = Restaurant(
    id = id,
    name = name,
    rating = rating,
    filterIds = filterIds,
    imageUrl = imageUrl,
    deliveryTimeMinutes = deliveryTimeMinutes
)