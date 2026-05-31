package com.umain.test.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.umain.test.data.api.BackendApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Singleton
    @Provides
    fun provideBackendApi(gson: Gson): BackendApi = Retrofit.Builder()
        .baseUrl("https://food-delivery.umain.io/api/v1/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(BackendApi::class.java)
}