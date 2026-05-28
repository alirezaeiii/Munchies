package com.umain.test.data.api

import com.umain.test.data.response.FilterResponse
import com.umain.test.data.response.RestaurantsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface BackendApi {
    @GET("restaurants")
    suspend fun getRestaurants(): RestaurantsResponse

    @GET("filter/{id}")
    suspend fun getFilter(@Path("id") id: String): FilterResponse
}