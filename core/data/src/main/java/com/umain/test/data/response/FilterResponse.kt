package com.umain.test.data.response

import com.google.gson.annotations.SerializedName
import com.umain.test.domain.model.Filter

data class FilterResponse(
    val id: String,
    val name: String,
    @SerializedName("image_url")
    val imageUrl: String
)

fun FilterResponse.asDomainModel() = Filter(
    id = id,
    name = name,
    imageUrl = imageUrl
)