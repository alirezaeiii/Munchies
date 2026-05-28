package com.umain.test

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.umain.test.common.ui.AppTheme
import com.umain.test.common.ui.common.Screens
import com.umain.test.common.ui.common.Screens.Companion.PROPERTY
import com.umain.test.domain.model.Restaurant
import com.umain.test.feature.details.DetailsScreen
import com.umain.test.feature.properties.RestaurantsScreen
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

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
    NavHost(navController, startDestination = Screens.Properties.title) {
        composable(Screens.Properties.title) {
            RestaurantsScreen(hiltViewModel()) { property ->
                val json = Uri.encode(
                    Gson().toJson(
                        property, object : TypeToken<Restaurant>() {}.type
                    )
                )
                navController.navigate(
                    Screens.Details.title.replace("{${PROPERTY}}", json)
                )
            }
        }
        composable(
            Screens.Details.title, arguments = listOf(
            navArgument(PROPERTY) {
                type = NavType.StringType
            })) { from ->
            DetailsScreen(
                Gson().fromJson(
                    from.arguments?.getString(PROPERTY),
                    object : TypeToken<Restaurant>() {}.type,
                )
            ) {
                navController.navigateUp()
            }
        }
    }
}