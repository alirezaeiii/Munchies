package com.umain.test.data.repository

import com.umain.test.data.api.BackendApi
import com.umain.test.data.response.asDomainModel
import com.umain.test.domain.model.Status
import com.umain.test.domain.repository.StatusRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatusRepositoryImpl @Inject constructor(
    private val backendApi: BackendApi
) : StatusRepository {

    override suspend fun getStatus(id: String): Status = backendApi.getStatus(id).asDomainModel()
}