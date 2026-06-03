package com.umain.test.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umain.test.common.ui.DarkText
import com.umain.test.common.ui.GrayText
import com.umain.test.common.ui.common.ErrorScreen
import com.umain.test.common.ui.common.ProgressScreen
import com.umain.test.common.ui.common.RestaurantImage
import com.umain.test.domain.model.Restaurant
import com.umain.test.common.R as commonR

@Composable
fun DetailsScreen(
    restaurant: Restaurant,
    viewModel: DetailViewModel,
    navigateUp: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> ProgressScreen()
            state.error != null ->
                ErrorScreen(stringResource(commonR.string.error_msg)) { viewModel.load() }

            else -> DetailsScreen(restaurant, state, navigateUp)
        }
    }
}

@OptIn(ExperimentalMotionApi::class)
@Composable
fun DetailsScreen(
    restaurant: Restaurant,
    state: DetailViewState,
    navigateUp: () -> Unit
) {
    val scroll = rememberScrollState(0)
    val big = 350.dp
    val small = 64.dp
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val scene = MotionScene {
        val (title, box, image, icon) = createRefsFor("title", "box", "image", "icon")

        val start1 = constraintSet {
            constrain(title) {
                bottom.linkTo(image.bottom)
                start.linkTo(image.start, 32.dp)
                customColor("color", Color.White)
            }
            constrain(image) {
                width = Dimension.matchParent
                height = Dimension.value(big)
                top.linkTo(parent.top)
                customColor("cover", Color(0x000000FF))
            }
            constrain(icon) {
                top.linkTo(image.top, 16.dp)
                start.linkTo(image.start, 16.dp)
                customColor("bg", Color.White)
                customColor("tint", Color.Black)
                width = Dimension.value(32.dp)
                height = Dimension.value(32.dp)
            }
            constrain(box) {
                top.linkTo(image.bottom, (-48).dp)
                centerHorizontallyTo(image)
            }
        }
        val end1 = constraintSet {
            constrain(title) {
                bottom.linkTo(image.bottom)
                start.linkTo(icon.end)
                centerVerticallyTo(image)
                scaleX = 0.7f
                scaleY = 0.7f
                customColor("color", onSurfaceColor)
            }
            constrain(image) {
                width = Dimension.matchParent
                height = Dimension.value(small)
                top.linkTo(parent.top)
                customColor("cover", surfaceColor)
            }
            constrain(icon) {
                top.linkTo(image.top, 8.dp)
                start.linkTo(image.start, 16.dp)
                customColor("bg", Color.Transparent)
                customColor("tint", onSurfaceColor)
            }
            constrain(box) {
                top.linkTo(image.bottom, (-20).dp)
                centerHorizontallyTo(image)
            }
        }
        transition(start1, end1, "default") {}
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(scroll)
    ) {
        Spacer(Modifier.height(big + 104.dp))
        repeat(2) {
            Text(
                text = LoremIpsum(222).values.first(),
                modifier = Modifier
                    .background(surfaceColor)
                    .padding(16.dp),
                color = onSurfaceColor
            )
        }
    }
    val gap = with(LocalDensity.current) { big.toPx() - small.toPx() }
    val progress = minOf(scroll.value / gap, 1f)

    MotionLayout(
        modifier = Modifier.fillMaxSize(),
        motionScene = scene,
        progress = progress
    ) {

        val coverColor = customColor("image", "cover")

        Box(
            modifier = Modifier
                .layoutId("image")
                .fillMaxSize()
        ) {
            RestaurantImage(
                thumb = restaurant.imageUrl,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(coverColor)
            )
        }
        IconButton(
            onClick = navigateUp,
            modifier = Modifier
                .layoutId("icon")
                .background(color = customColor("icon", "bg"), shape = CircleShape)
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = "back",
                tint = customColor("icon", "tint")
            )
        }
        Text(
            modifier = Modifier
                .layoutId("title")
                .padding(vertical = 40.dp, horizontal = 12.dp),
            text = restaurant.name,
            fontSize = 30.sp,
            color = customColor("title", "color")
        )
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .layoutId("box"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                Spacer(Modifier.height(10.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    text = restaurant.name,
                    fontSize = 20.sp,
                    color = DarkText
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    text = restaurant.filterNames.joinToString(separator = " - "),
                    fontSize = 14.sp,
                    color = GrayText
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    text = stringResource(if (state.isOpen) R.string.open else R.string.close),
                    fontSize = 17.sp,
                    color = if (state.isOpen) Color.Green else Color.Red
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}