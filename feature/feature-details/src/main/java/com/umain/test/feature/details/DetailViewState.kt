package com.umain.test.feature.details

data class DetailViewState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isOpen: Boolean = false
)