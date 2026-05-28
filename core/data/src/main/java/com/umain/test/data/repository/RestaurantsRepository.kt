package com.umain.test.data.repository

import android.content.Context
import com.umain.test.common.base.BaseRepository
import com.umain.test.data.api.BackendApi
import com.umain.test.data.database.FilterEntityDao
import com.umain.test.data.database.RestaurantEntityDao
import com.umain.test.data.database.asDatabaseModel
import com.umain.test.data.database.asDomainModel
import com.umain.test.data.di.IoDispatcher
import com.umain.test.data.response.asDomainModel
import com.umain.test.domain.model.Restaurant
import com.umain.test.domain.model.RestaurantsWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantsRepository @Inject constructor(
    private val backendApi: BackendApi,
    private val restaurantDao: RestaurantEntityDao,
    private val filterDao: FilterEntityDao,
    @ApplicationContext context: Context,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseRepository<RestaurantsWrapper, Nothing, Nothing>(context, dispatcher) {

    override suspend fun query(queryValue: Nothing?) =
        restaurantDao.getAll()
            .takeIf { it.isNotEmpty() }
            ?.let {
                RestaurantsWrapper(
                    it.asDomainModel(),
                    filterDao.getAll().asDomainModel()
                )
            }


    override suspend fun fetch(fetchValue: Nothing?): RestaurantsWrapper {
        val restaurants = backendApi
            .getRestaurants()
            .restaurants
            .asDomainModel()

        val filterIds = restaurants
            .flatMap(Restaurant::filterIds)
            .distinct()

        val filters = coroutineScope {
            filterIds
                .map { filterId ->
                    async {
                        backendApi.getFilter(filterId).asDomainModel()
                    }
                }
                .awaitAll()
        }
        return RestaurantsWrapper(restaurants, filters)
    }

    override suspend fun saveFetchResult(item: RestaurantsWrapper) {
        restaurantDao.insertAll(item.restaurants.asDatabaseModel())
        filterDao.insertAll(item.filters.asDatabaseModel())
    }
}