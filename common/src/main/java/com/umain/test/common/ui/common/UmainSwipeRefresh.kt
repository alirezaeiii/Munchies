package com.umain.test.common.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.umain.test.common.base.BaseScreenState

@Composable
fun <TYPE, STATE : BaseScreenState<TYPE, STATE>> UmainSwipeRefresh(
    modifier: Modifier = Modifier,
    state: STATE,
    refresh: () -> Unit,
    isRefreshing: Boolean = state.base.isRefreshing,
    mainContent: @Composable () -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { refresh.invoke() },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state,
                trigger
            )
        },
        modifier = modifier.fillMaxSize()
    ) {
        mainContent()
    }
}