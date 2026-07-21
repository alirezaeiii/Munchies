package com.umain.test.feature.details

import androidx.lifecycle.SavedStateHandle
import com.umain.test.common.ui.common.Routes
import com.umain.test.domain.model.Restaurant
import com.umain.test.domain.model.Status
import com.umain.test.domain.repository.StatusRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<StatusRepository>()
    private val savedStateHandle = mockk<SavedStateHandle>()
    private lateinit var viewModel: DetailViewModel

    private val sampleRestaurant = Restaurant(
        id = "1",
        name = "Test Restaurant",
        rating = 4.5f,
        filterIds = listOf("1"),
        imageUrl = "url",
        deliveryTimeMinutes = 30
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { savedStateHandle.get<Restaurant>(Routes.RESTAURANT) } returns sampleRestaurant
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load status successfully updates state`() = runTest {
        coEvery { repository.getStatus("1") } returns Status("1", true)

        viewModel = DetailViewModel(repository, savedStateHandle)
        
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.isOpen)
        assertNull(state.error)
        assertNull(state.errorMessage)
    }

    @Test
    fun `load status failure updates state with error`() = runTest {
        val exception = RuntimeException("Network Error")
        coEvery { repository.getStatus("1") } throws exception

        viewModel = DetailViewModel(repository, savedStateHandle)
        
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertFalse(state.isOpen)
        assertEquals(exception, state.error)
        // errorMessage is null because it's not a 404 HttpException
        assertNull(state.errorMessage)
    }
}
