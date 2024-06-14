package com.darekbx.nwsweatheralerts.ui.alerts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.nwsweatheralerts.R
import com.darekbx.nwsweatheralerts.repository.remote.Properties
import com.darekbx.nwsweatheralerts.repository.remote.Response
import com.darekbx.nwsweatheralerts.ui.NWSViewModel
import com.darekbx.nwsweatheralerts.ui.UiState
import com.darekbx.nwsweatheralerts.ui.theme.NWSWeatherAlertsTheme
import com.darekbx.nwsweatheralerts.utils.DateTimeFormatter
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlertsScreen(nwsViewModel: NWSViewModel = koinViewModel()) {
    val uiState by nwsViewModel.uiState
    val pullRefreshState = rememberPullRefreshState(
        uiState == UiState.Loading,
        { nwsViewModel.getAlerts() }
    )

    LaunchedEffect(Unit) {
        nwsViewModel.getAlerts()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {
        uiState.let {
            when (it) {
                is UiState.Done -> AlertsView(it.result)
                is UiState.Failed -> FailedMessage(it.e)
                UiState.Loading -> LoadingProgress()
                UiState.Idle -> {}
            }
        }

        PullRefreshIndicator(
            refreshing = uiState == UiState.Loading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlertsView(response: Response) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    text = stringResource(id = R.string.title),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.update_time),
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        modifier = Modifier.padding(start = 2.dp),
                        text = DateTimeFormatter.format(response.updated),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                HorizontalDivider()
            }
        }
        items(response.features, key = { it.id!! }) {
            it.properties?.let { properties ->
                PropertyView(properties)
            }
        }
    }
}

@Composable
private fun PropertyView(properties: Properties) {
    var isExpanded by remember { mutableStateOf(false) }
    val isTornado = remember { properties.description.contains("tornado") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp),
                text = properties.event,
                style = MaterialTheme.typography.titleMedium,
                color = if (isTornado) Color.Red else Color.Unspecified
            )
            Text(
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp),
                text = DateTimeFormatter.format(properties.effective!!),
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
                .clickable { isExpanded = !isExpanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon =
                if (isExpanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown
            Icon(
                modifier = Modifier.padding(end = 4.dp),
                imageVector = icon,
                contentDescription = "exapnd"
            )
            Text(text = properties.headline, style = MaterialTheme.typography.bodyMedium)
        }
        if (isExpanded) {
            Text(
                text = properties.description.replace("\n", ""),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
                    .background(Color.LightGray.copy(alpha = 0.5F), RoundedCornerShape(4.dp))
                    .padding(4.dp)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun LoadingProgress() {
    CircularProgressIndicator(Modifier.size(64.dp))
}

@Composable
fun FailedMessage(e: Exception) {
    Text(
        modifier = Modifier.padding(64.dp),
        textAlign = TextAlign.Center,
        text = e.toString(),
        color = Color.Red.copy(alpha = 0.8F)
    )
}

@Preview
@Composable
fun PropertyViewPreview() {
    NWSWeatherAlertsTheme {
        PropertyView(
            Properties(
                "wx:Alert",
                "",
                "Pointe Coupee; Iberville; West Baton Rouge; East Baton Rouge; Northern Livingston; Southern Livingston; Western Ascension; Eastern Ascension",
                "2024-06-14T04:14:00-04:00",
                "",
                "",
                "Alert",
                "Met",
                "",
                "",
                "",
                "Air Quality Alert",
                "NWS New Orleans LA",
                "Air Quality Alert issued June 13 at 4:28AM CDT by NWS New Orleans LA",
                "he Louisiana Department of Environmental Quality is forecasting an\\nOzone Advisory Day for East Baton Rouge, West Baton Rouge,\\nLivingston, Ascension, Iberville, and Pointe Coupee Parishes, .\\n\\nThe Air Quality Index indicates that ozone will be at the Orange\\nlevel, which is unhealthy for sensitive groups.",
                ""
            )
        )
    }
}

@Preview
@Composable
fun FailedMessagePreview() {
    NWSWeatherAlertsTheme {
        FailedMessage(e = IllegalStateException("Unknown exception"))
    }
}

@Preview
@Composable
fun LoadingProgressPreview() {
    NWSWeatherAlertsTheme {
        LoadingProgress()
    }
}