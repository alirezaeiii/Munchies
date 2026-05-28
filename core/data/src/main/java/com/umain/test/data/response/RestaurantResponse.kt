package com.umain.test.data.response

import com.umain.test.domain.model.Restaurant
import com.squareup.moshi.Json

data class RestaurantResponse(
    val id: String,
    val name: String,
    val rating: Float,
    val filterIds: List<String>,
    @param:Json(name = "image_url")
    val imageUrl: String?,
    @param:Json(name = "delivery_time_minutes")
    val deliveryTimeMinutes: String?
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