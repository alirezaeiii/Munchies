package com.umain.test.common.base

interface UiEvent {
    interface Warning : UiEvent {
        val message: String
    }
    interface Navigation : UiEvent {
        val route: String
    }
    interface NavigateUp : UiEvent
}