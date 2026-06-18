package com.umain.test

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.umain.test.common.ui.AppTheme
import com.umain.test.common.ui.common.Routes
import com.umain.test.common.ui.common.Routes.Companion.RESTAURANT
import com.umain.test.domain.model.Restaurant
import com.umain.test.feature.details.DetailsScreen
import com.umain.test.feature.restaurants.RestaurantsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            AppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    NavGraph(navController)
                }
            }
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = Routes.Restaurants.title) {
        composable(Routes.Restaurants.title) {
            RestaurantsScreen(hiltViewModel()) { restaurant ->
                val json = Uri.encode(Gson().toJson(restaurant))
                navController.navigate(
                    Routes.Details.title.replace("{${RESTAURANT}}", json)
                )
            }
        }
        composable(
            Routes.Details.title, arguments = listOf(
                navArgument(RESTAURANT) {
                    type = RestaurantNavType()
                })
        ) { from ->
            val restaurant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                from.arguments?.getParcelable(RESTAURANT, Restaurant::class.java)
            } else {
                @Suppress("DEPRECATION")
                from.arguments?.getParcelable(RESTAURANT)
            }
            DetailsScreen(
                restaurant!!,
                hiltViewModel(),
                navController::navigateUp
            )
        }
    }
}