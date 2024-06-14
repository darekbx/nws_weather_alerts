package com.darekbx.shoppinglist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.darekbx.nwsweatheralerts.ui.alerts.AlertsScreen
import com.darekbx.nwsweatheralerts.ui.maps.MapScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ListDestination.route,
        modifier = modifier
    ) {
        composable(route = ListDestination.route) {
            AlertsScreen()
        }

        composable(route = MapDestination.route) {
            MapScreen()
        }
    }
}

fun NavController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
