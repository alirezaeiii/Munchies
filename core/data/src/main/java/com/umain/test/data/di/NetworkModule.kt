package com.umain.test.data.di

import com.umain.test.data.api.BackendApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .build()

    @Singleton
    @Provides
    fun provideBackendApi(moshi: Moshi): BackendApi = Retrofit.Builder()
        .baseUrl("https://food-delivery.umain.io/api/v1/")
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .build()
        .create(BackendApi::class.java)
}