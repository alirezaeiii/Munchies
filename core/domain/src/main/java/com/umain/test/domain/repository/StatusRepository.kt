package com.umain.test.domain.repository

import com.umain.test.domain.model.Status

interface StatusRepository {
    suspend fun getStatus(id: String): Status
}