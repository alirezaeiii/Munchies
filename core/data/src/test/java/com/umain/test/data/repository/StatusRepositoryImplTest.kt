package com.umain.test.data.repository

import com.umain.test.data.api.BackendApi
import com.umain.test.data.response.StatusResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StatusRepositoryImplTest {

    private val backendApi = mockk<BackendApi>()
    private lateinit var repository: StatusRepositoryImpl

    @Before
    fun setup() {
        repository = StatusRepositoryImpl(backendApi)
    }

    @Test
    fun `getStatus returns correctly mapped status`() = runTest {
        val restaurantId = "123"
        val statusResponse = StatusResponse(id = restaurantId, isCurrentlyOpen = true)
        coEvery { backendApi.getStatus(restaurantId) } returns statusResponse

        val result = repository.getStatus(restaurantId)

        assertEquals(restaurantId, result.id)
        assertEquals(true, result.isCurrentlyOpen)
    }

    @Test
    fun `getStatus propagates API failure`() = runTest {
        val restaurantId = "123"
        val exception = RuntimeException("API Error")
        coEvery { backendApi.getStatus(restaurantId) } throws exception

        assertFailsWith<RuntimeException> {
            repository.getStatus(restaurantId)
        }
    }
}
