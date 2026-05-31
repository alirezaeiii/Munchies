package com.umain.test.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.umain.test.domain.model.Restaurant

@Entity(tableName = "restaurant")
data class RestaurantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val rating: Float,
    val filterIds: List<String>,
    val filterNames: List<String>,
    val imageUrl: String,
    val deliveryTimeMinutes: Int
)

fun List<RestaurantEntity>.asDomainModel() = map(RestaurantEntity::asDomainModel)

fun List<Restaurant>.asDatabaseModel() = map(Restaurant::asDatabaseModel)

private fun RestaurantEntity.asDomainModel() = Restaurant(
    id = id,
    name = name,
    rating = rating,
    filterIds = filterIds,
    filterNames = filterNames,
    imageUrl = imageUrl,
    deliveryTimeMinutes = deliveryTimeMinutes
)

private fun Restaurant.asDatabaseModel() = RestaurantEntity(
    id = id,
    name = name,
    rating = rating,
    filterIds = filterIds,
    filterNames = filterNames,
    imageUrl = imageUrl,
    deliveryTimeMinutes = deliveryTimeMinutes
)
