package com.umain.test.data.response

import com.google.gson.annotations.SerializedName
import com.umain.test.domain.model.Status

data class StatusResponse(
    @SerializedName("restaurant_id")
    val id: String,
    @SerializedName("is_currently_open")
    val isCurrentlyOpen: Boolean
)

fun StatusResponse.asDomainModel() = Status(
    id = id,
    isCurrentlyOpen = isCurrentlyOpen
)