package com.umain.test.feature.restaurants

import com.umain.test.common.base.BaseRepository
import com.umain.test.common.base.BaseViewModel
import com.umain.test.common.utils.Async
import com.umain.test.domain.model.Filter
import com.umain.test.domain.model.Restaurant
import com.umain.test.domain.model.RestaurantsWrapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = mockk<BaseRepository<RestaurantsWrapper, Nothing, Nothing>>()
    private lateinit var viewModel: RestaurantsViewModel

    private val sampleFilters = listOf(
        Filter("1", "Filter 1", "url1"),
        Filter("2", "Filter 2", "url2")
    )
    private val sampleRestaurants = listOf(
        Restaurant("r1", "Restaurant 1", 4.5f, listOf("1"), emptyList(), "url1", 30),
        Restaurant("r2", "Restaurant 2", 4.0f, listOf("2"), emptyList(), "url2", 40),
        Restaurant("r3", "Restaurant 3", 3.5f, listOf("1", "2"), emptyList(), "url3", 20)
    )
    private val sampleWrapper = RestaurantsWrapper(sampleRestaurants, sampleFilters)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Mock getResult to return success by default so init { refresh() } works
        every { repository.getResult(null, null, any()) } returns flowOf(Async.Success(sampleWrapper))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should fetch restaurants and update state`() = runTest {
        viewModel = RestaurantsViewModel(repository)
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.state.value
        assertEquals(sampleWrapper, state.base.items)
        assertEquals(sampleRestaurants, state.filteredRestaurants)
        assertTrue(state.activeFilters.isEmpty())
        verify { repository.getResult(null, null, true) }
    }

    @Test
    fun `onFilterChanged should filter restaurants correctly`() = runTest {
        viewModel = RestaurantsViewModel(repository)
        testDispatcher.scheduler.runCurrent()

        // Filter by "1"
        viewModel.onFilterChanged(listOf("1"))
        testDispatcher.scheduler.runCurrent()

        var state = viewModel.state.value
        assertEquals(listOf("1"), state.activeFilters)
        assertEquals(2, state.filteredRestaurants.size)
        assertTrue(state.filteredRestaurants.any { it.id == "r1" })
        assertTrue(state.filteredRestaurants.any { it.id == "r3" })

        // Filter by both "1" and "2"
        viewModel.onFilterChanged(listOf("1", "2"))
        testDispatcher.scheduler.runCurrent()

        state = viewModel.state.value
        assertEquals(listOf("1", "2"), state.activeFilters)
        assertEquals(1, state.filteredRestaurants.size)
        assertEquals("r3", state.filteredRestaurants[0].id)
    }

    @Test
    fun `onFilterChanged with empty list should show all restaurants`() = runTest {
        viewModel = RestaurantsViewModel(repository)
        testDispatcher.scheduler.runCurrent()

        viewModel.onFilterChanged(listOf("1"))
        testDispatcher.scheduler.runCurrent()

        viewModel.onFilterChanged(emptyList())
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.state.value
        assertTrue(state.activeFilters.isEmpty())
        assertEquals(sampleRestaurants, state.filteredRestaurants)
    }

    @Test
    fun `fetch error should update state with error message`() = runTest {
        val errorMessage = "Network Error"
        every { repository.getResult(null, null, any()) } returns flowOf(Async.Error(errorMessage))

        viewModel = RestaurantsViewModel(repository)
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.state.value
        assertEquals(errorMessage, state.base.error)
        assertEquals(false, state.base.isLoading)
    }

    @Test
    fun `fetch warning error should update state and emit warning event`() = runTest {
        val warningMessage = "Cache hit with warning"
        val repositoryFlow = MutableSharedFlow<Async<RestaurantsWrapper>>()
        every { repository.getResult(null, null, any()) } returns repositoryFlow

        viewModel = RestaurantsViewModel(repository)

        val events = mutableListOf<BaseViewModel.UiEvent>()
        val collectJob = launch {
            viewModel.showWarningUiEvent.collect { events.add(it) }
        }

        // Trigger emission
        launch {
            repositoryFlow.emit(Async.Error(warningMessage, isWarning = true))
        }
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.state.value
        assertEquals(warningMessage, state.base.error)
        assertTrue(state.base.isWarning)

        assertTrue(events.any { it is BaseViewModel.UiEvent.ShowWarning && it.message == warningMessage }, "Event not emitted")
        
        collectJob.cancel()
    }
}
