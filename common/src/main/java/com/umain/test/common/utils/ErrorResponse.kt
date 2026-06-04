package com.umain.test.common.utils

import com.google.gson.Gson
import retrofit2.HttpException

data class ErrorResponse(
    val error: Boolean,
    val reason: String
)

fun Throwable.getErrorMessage(): String? =
    if (this is HttpException && this.code() == 404) {
        val errorBody = this.response()?.errorBody()?.string()
        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
        errorResponse?.reason
    } else {
        null
    }