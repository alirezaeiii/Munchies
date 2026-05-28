package com.umain.test.common.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umain.test.common.ui.common.ErrorScreen
import com.umain.test.common.ui.common.ProgressScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <TYPE, STATE : BaseScreenState<TYPE>, QueryType, FetchType> Content(
    viewModel: BaseViewModel<TYPE, STATE, QueryType, FetchType>,
    snackbarHostState: SnackbarHostState,
    mainContent: @Composable (STATE) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.base.isLoading -> ProgressScreen()
            state.base.error.isNotEmpty() && !state.base.isWarning ->
                ErrorScreen(state.base.error) { viewModel.refresh() }

            else -> mainContent(state)
        }
        LaunchedEffect(Unit) {
            viewModel.showWarningUiEvent.collectLatest { event ->
                when (event) {
                    is BaseViewModel.UiEvent.ShowWarning ->
                        snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
}