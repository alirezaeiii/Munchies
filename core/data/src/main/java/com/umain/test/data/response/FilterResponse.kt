package com.umain.test.data.response

import com.umain.test.domain.model.Filter
import com.squareup.moshi.Json

data class FilterResponse(
    val id: String,
    val name: String,
    @param:Json(name = "image_url")
    val imageUrl: String?
)

fun FilterResponse.asDomainModel() = Filter(
    id = id,
    name = name,
    imageUrl = imageUrl
)