package com.darekbx.nwsweatheralerts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.darekbx.nwsweatheralerts.ui.theme.NWSWeatherAlertsTheme
import com.darekbx.shoppinglist.navigation.AppNavHost
import com.darekbx.shoppinglist.navigation.ListDestination
import com.darekbx.shoppinglist.navigation.MapDestination
import com.darekbx.shoppinglist.navigation.navigateSingleTopTo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NWSWeatherAlertsTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = { },
                    content = { innerPadding ->
                        AppNavHost(
                            modifier = Modifier.padding(innerPadding),
                            navController = navController
                        )
                    },
                    bottomBar = { BottomNavigation(navController) }
                )
            }
        }
    }

    @Composable
    private fun BottomNavigation(navController: NavController) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                modifier = Modifier
                    .clickable { navController.navigateSingleTopTo(ListDestination.route) }
                    .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "list"
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.list),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Column(
                modifier = Modifier
                    .clickable { navController.navigateSingleTopTo(MapDestination.route) }
                    .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_map),
                    contentDescription = "map"
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.map),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
