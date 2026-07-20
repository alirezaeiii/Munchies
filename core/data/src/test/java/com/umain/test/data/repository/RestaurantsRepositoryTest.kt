package com.umain.test.data.repository

import android.content.Context
import app.cash.turbine.test
import com.umain.test.common.utils.Async
import com.umain.test.data.api.BackendApi
import com.umain.test.data.database.FilterEntityDao
import com.umain.test.data.database.RestaurantEntity
import com.umain.test.data.database.RestaurantEntityDao
import com.umain.test.data.response.FilterResponse
import com.umain.test.data.response.RestaurantResponse
import com.umain.test.data.response.RestaurantsResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantsRepositoryTest {

    private val backendApi = mockk<BackendApi>()
    private val restaurantDao = mockk<RestaurantEntityDao>(relaxed = true)
    private val filterDao = mockk<FilterEntityDao>(relaxed = true)
    private val context = mockk<Context>()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: RestaurantsRepository

    @Before
    fun setup() {
        repository = RestaurantsRepository(
            backendApi,
            restaurantDao,
            filterDao,
            context,
            testDispatcher
        )
        
        // Mock strings for BaseRepository error messages
        every { context.getString(any()) } returns "Error message"
    }

    @Test
    fun `getResult emits loading and success when data is in DB and no refresh needed`() = runTest(testDispatcher) {
        // Mock data in DB
        coEvery { restaurantDao.getAll() } returns listOf(mockk(relaxed = true))
        coEvery { filterDao.getAll() } returns listOf(mockk(relaxed = true))

        repository.getResult(forceRefresh = false).test {
            assertTrue(awaitItem() is Async.Loading)
            assertTrue(awaitItem() is Async.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getResult performs fetch when DB is empty`() = runTest(testDispatcher) {
        // Mock empty DB then non-empty DB after fetch
        coEvery { restaurantDao.getAll() } returnsMany listOf(emptyList(), listOf(mockk(relaxed = true)))
        coEvery { filterDao.getAll() } returnsMany listOf(emptyList(), listOf(mockk(relaxed = true)))
        
        // Mock API response
        val restaurantResponse = RestaurantResponse("1", "R1", 4.5f, listOf("f1"), "url", 30)
        coEvery { backendApi.getRestaurants() } returns RestaurantsResponse(listOf(restaurantResponse))
        coEvery { backendApi.getFilter("f1") } returns FilterResponse("f1", "Filter 1", "url")
        
        repository.getResult().test {
            assertTrue(awaitItem() is Async.Loading)
            // DB was empty, so it fetches and then emits Success
            assertTrue(awaitItem() is Async.Success)
            
            coVerify { backendApi.getRestaurants() }
            coVerify { backendApi.getFilter("f1") }
            coVerify { restaurantDao.insertAll(any()) }
            coVerify { filterDao.insertAll(any()) }
            
            awaitComplete()
        }
    }

    @Test
    fun `getResult emits cached data then refreshes when forceRefresh is true`() = runTest(testDispatcher) {
        val cachedRestaurant = mockk<RestaurantEntity>(relaxed = true)
        val newRestaurant = mockk<RestaurantEntity>(relaxed = true)
        
        coEvery { restaurantDao.getAll() } returnsMany listOf(listOf(cachedRestaurant), listOf(newRestaurant))
        coEvery { filterDao.getAll() } returns listOf(mockk(relaxed = true))

        val restaurantResponse = RestaurantResponse("1", "R1", 4.5f, listOf("f1"), "url", 30)
        coEvery { backendApi.getRestaurants() } returns RestaurantsResponse(listOf(restaurantResponse))
        coEvery { backendApi.getFilter("f1") } returns FilterResponse("f1", "Filter 1", "url")

        repository.getResult(forceRefresh = true).test {
            assertTrue(awaitItem() is Async.Loading)
            val cacheSuccess = awaitItem()
            assertTrue(cacheSuccess is Async.Success)
            
            val refreshLoading = awaitItem()
            assertTrue(refreshLoading is Async.Loading)
            assertTrue(refreshLoading.isRefreshing)
            
            val finalSuccess = awaitItem()
            assertTrue(finalSuccess is Async.Success)

            coVerify { backendApi.getRestaurants() }
            coVerify { backendApi.getFilter("f1") }
            coVerify { restaurantDao.insertAll(any()) }
            coVerify { filterDao.insertAll(any()) }
            awaitComplete()
        }
    }

    @Test
    fun `getResult emits error when API fails and DB is empty`() = runTest(testDispatcher) {
        coEvery { restaurantDao.getAll() } returns emptyList()
        coEvery { backendApi.getRestaurants() } throws Exception("API Error")

        repository.getResult().test {
            assertTrue(awaitItem() is Async.Loading)
            val error = awaitItem()
            assertTrue(error is Async.Error)
            assertEquals(false, error.isWarning)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getResult emits warning error when API fails and DB has data`() = runTest(testDispatcher) {
        coEvery { restaurantDao.getAll() } returns listOf(mockk(relaxed = true))
        coEvery { filterDao.getAll() } returns listOf(mockk(relaxed = true))
        coEvery { backendApi.getRestaurants() } throws Exception("API Error")

        repository.getResult(forceRefresh = true).test {
            assertTrue(awaitItem() is Async.Loading) // Initial Loading
            assertTrue(awaitItem() is Async.Success) // Cache success
            val loadingRefresh = awaitItem()
            assertTrue(loadingRefresh is Async.Loading) // Refresh loading
            assertTrue(loadingRefresh.isRefreshing)
            
            val error = awaitItem()
            assertTrue(error is Async.Error)
            assertTrue(error.isWarning)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
