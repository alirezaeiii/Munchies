package com.umain.test.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.umain.test.domain.model.Filter

@Entity(tableName = "filters")
data class FilterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String
)

fun List<FilterEntity>.asDomainModel() = map(FilterEntity::asDomainModel)

fun List<Filter>.asDatabaseModel() = map(Filter::asDatabaseModel)

private fun FilterEntity.asDomainModel() = Filter(
    id = id,
    name = name,
    imageUrl = imageUrl
)

private fun Filter.asDatabaseModel() = FilterEntity(
    id = id,
    name = name,
    imageUrl = imageUrl
)