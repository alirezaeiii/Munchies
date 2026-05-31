package com.umain.test.feature.restaurants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.umain.test.common.base.Content
import com.umain.test.common.ui.CardBackground
import com.umain.test.common.ui.DarkText
import com.umain.test.common.ui.Rating
import com.umain.test.common.ui.Subtitle
import com.umain.test.common.ui.common.UmainSwipeRefresh
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

                CustomFilterChipRow(
                    allFilters = state.base.items?.allFilters ?: emptyList(),
                    activeFilters = state.activeFilters,
                    onSelectionChanged = { selectedItems ->
                        viewModel.onFilterChanged(selectedItems)
                    }
                )

                UmainSwipeRefresh(
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

@Composable
fun CustomFilterChipRow(
    allFilters: List<Filter>,
    activeFilters: List<String>,
    onSelectionChanged: (List<String>) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items = allFilters) { filter ->
            val isSelected = filter.id in activeFilters
            CustomFilterChip(
                text = filter.name,
                imageUrl = filter.imageUrl,
                isSelected = isSelected,
                onClick = {
                    val newSelection = if (isSelected) {
                        activeFilters.filter { it != filter.id }
                    } else {
                        activeFilters + filter.id
                    }
                    onSelectionChanged(newSelection)
                }
            )
        }
    }
}

@Composable
fun CustomFilterChip(
    text: String,
    imageUrl: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = onClick,
        selected = isSelected,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        modifier = modifier
            .width(180.dp)
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.background,
            selectedContainerColor = Color(0xFFE3F2FD),
            labelColor = Color.Black,
            selectedLabelColor = Color(0xFF1976D2)
        ),
        shape = RoundedCornerShape(24.dp),
        border = null
    )
}


@Composable
fun RestaurantsScreenContent(
    filteredRestaurants: List<Restaurant>,
    navigateToDetail: (Restaurant) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        items(filteredRestaurants) { restaurant ->
            RestaurantRowComposable(restaurant, navigateToDetail)
        }
    }
}

@Composable
fun RestaurantRowComposable(
    restaurant: Restaurant,
    navigateToDetail: (Restaurant) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomEnd = 0.dp,
            bottomStart = 0.dp
        ),
        onClick = { navigateToDetail(restaurant) }
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(restaurant.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp),
            contentScale = ContentScale.FillWidth
        )
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = restaurant.name,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = restaurant.filterNames.joinToString(separator = " - "),
                    color = Subtitle
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    /*Image(
                        painter = painterResource(id = R.drawable.),
                        contentDescription = null,
                        modifier = Modifier.size(width = 10.dp, height = 10.dp)
                    )*/
                    Text(
                        text = String.format("%d mins", restaurant.deliveryTimeMinutes),
                        color = Rating,
                        modifier = Modifier.padding(start = 3.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                /*Image(
                    painter = painterResource(id = R.drawable.m3_split_button_chevron_avd),
                    contentDescription = null,
                    modifier = Modifier.size(width = 12.dp, height = 12.dp)
                )*/
                Text(
                    text = String.format("%.1f", restaurant.rating),
                    color = Rating,
                    modifier = Modifier.padding(start = 3.dp)
                )
            }
        }
    }
}