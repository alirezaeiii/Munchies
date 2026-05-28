package com.umain.test.data.di

import com.umain.test.common.base.BaseRepository
import com.umain.test.data.repository.RestaurantsRepository
import com.umain.test.domain.model.RestaurantsWrapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    @JvmSuppressWildcards
    internal abstract fun bindRepository(repository: RestaurantsRepository): BaseRepository<RestaurantsWrapper, Nothing, Nothing>
}