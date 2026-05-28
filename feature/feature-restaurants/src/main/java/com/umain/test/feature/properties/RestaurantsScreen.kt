package com.umain.test.feature.properties

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.umain.test.common.base.Content
import com.umain.test.common.ui.common.PropertiesSwipeRefresh
import com.umain.test.domain.model.Filter
import com.umain.test.domain.model.Restaurant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantsScreen(
    viewModel: RestaurantsViewModel,
    navigateToDetail: (Restaurant) -> Unit
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.umain))
                }
            )
        }
    ) { paddingValues ->

        Content(
            viewModel = viewModel,
            snackbarHostState = snackbarHostState
        ) { state ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {

                MultiSelectChips(
                    items = state.base.items?.filters,
                    onSelectionChanged = { selectedItems ->
                        viewModel.onFilterChanged(selectedItems)
                    }
                )

                PropertiesSwipeRefresh(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    viewModel = viewModel,
                    state = state,
                ) {
                    RestaurantsScreenContent(
                        state.filteredRestaurants,
                        navigateToDetail
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiSelectChips(
    items: List<Filter>?,
    onSelectionChanged: (List<String>) -> Unit
) {
    val selectedItems = remember {
        mutableStateListOf<String>()
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items?.forEach { item ->

            val isSelected = item.id in selectedItems

            FilterChip(
                selected = isSelected,
                onClick = {

                    if (isSelected) {
                        selectedItems.remove(item.id)
                    } else {
                        selectedItems.add(item.id)
                    }

                    onSelectionChanged(selectedItems.toList())
                },
                label = {
                    Text(item.name)
                },
                leadingIcon = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(
                                FilterChipDefaults.IconSize
                            )
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
fun RestaurantsScreenContent(
    filteredProperties: List<Restaurant>,
    navigateToDetail: (Restaurant) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(filteredProperties) { property ->
            RestaurantRowComposable(property, navigateToDetail)
        }
    }
}

@Composable
fun RestaurantRowComposable(
    restaurant: Restaurant,
    navigateToDetail: (Restaurant) -> Unit
) {
    Column {
        Text(restaurant.name)
        Text(restaurant.rating.toString())
    }
}