package com.umain.test.feature.restaurants

import androidx.compose.foundation.Image
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.umain.test.common.base.Content
import com.umain.test.common.ui.CardBackground
import com.umain.test.common.ui.DarkText
import com.umain.test.common.ui.GrayText
import com.umain.test.common.ui.common.UmainSwipeRefresh
import com.umain.test.common.ui.selectedContainerColor
import com.umain.test.common.ui.selectedLabelColor
import com.umain.test.common.ui.subTitleText
import com.umain.test.domain.model.Filter
import com.umain.test.domain.model.Restaurant
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantsScreen(
    viewModel: RestaurantsViewModel,
    navigateToDetail: (Restaurant) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_umain),
                            contentDescription = "Banner",
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
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
            selectedContainerColor = selectedContainerColor,
            labelColor = MaterialTheme.colorScheme.onBackground,
            selectedLabelColor = selectedLabelColor
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
    if (filteredRestaurants.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NoDataFoundAnimation(modifier = Modifier.size(200.dp))
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(filteredRestaurants) { restaurant ->
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
                                color = GrayText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_clock),
                                    contentDescription = null,
                                    modifier = Modifier.size(width = 10.dp, height = 10.dp)
                                )
                                Text(
                                    text = String.format(
                                        Locale.ROOT,
                                        "%d mins",
                                        restaurant.deliveryTimeMinutes
                                    ),
                                    color = subTitleText,
                                    modifier = Modifier.padding(start = 3.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_star),
                                contentDescription = null,
                                modifier = Modifier.size(width = 12.dp, height = 12.dp)
                            )
                            Text(
                                text = String.format(Locale.ROOT, "%.1f", restaurant.rating),
                                color = subTitleText,
                                modifier = Modifier.padding(start = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoDataFoundAnimation(modifier: Modifier = Modifier) {
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.no_data_found,
        ),
    )
    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
    )
    LottieAnimation(
        composition = preloaderLottieComposition,
        progress = preloaderProgress,
        modifier = modifier,
    )
}