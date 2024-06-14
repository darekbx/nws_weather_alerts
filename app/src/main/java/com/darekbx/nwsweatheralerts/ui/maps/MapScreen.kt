package com.darekbx.nwsweatheralerts.ui.maps

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.darekbx.nwsweatheralerts.BuildConfig
import com.darekbx.nwsweatheralerts.repository.remote.Features
import com.darekbx.nwsweatheralerts.repository.remote.Response
import com.darekbx.nwsweatheralerts.ui.NWSViewModel
import com.darekbx.nwsweatheralerts.ui.UiState
import com.darekbx.nwsweatheralerts.ui.alerts.FailedMessage
import com.darekbx.nwsweatheralerts.ui.alerts.LoadingProgress
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon

@Composable
fun MapScreen(nwsViewModel: NWSViewModel = koinViewModel()) {
    val uiState by nwsViewModel.uiState

    LaunchedEffect(Unit) {
        nwsViewModel.getAlerts()
    }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        uiState.let {
            when (it) {
                is UiState.Done -> MapView(it.result)
                is UiState.Failed -> FailedMessage(it.e)
                UiState.Loading -> LoadingProgress()
                UiState.Idle -> {}
            }
        }
    }
}

@Composable
private fun MapView(response: Response) {
    val context = LocalContext.current
    val mapView = rememberMapWithLifecycle()
    val zoomToPlace = 6.0
    var selectFeature by remember { mutableStateOf<Features?>(null) }

    AndroidView(factory = { mapView }) { map ->
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(zoomToPlace)

        var isCenterSet = false

        response.features.forEach { feature ->
            feature.geometry?.let {
                val points = mutableListOf<GeoPoint>()
                it.coordinates.firstOrNull()?.forEach { pointMap ->
                    val lat = pointMap[1]
                    val lng = pointMap[0]
                    points.add(GeoPoint(lat, lng))
                }
                drawLine(points, map, Color.RED) {
                    selectFeature = feature
                }

                if (!isCenterSet) {
                    map.controller.setCenter(points[0])
                    isCenterSet = true
                }
            }
        }
    }

    if (selectFeature != null) {
        AlertDialog(
            onDismissRequest = { selectFeature = null },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        text = selectFeature?.properties?.event ?: "",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier
                            .height(200.dp)
                            .verticalScroll(rememberScrollState()),
                        text = selectFeature?.properties?.description ?: ""
                    )
                }
            },
            confirmButton = { },
            dismissButton = {
                Button(onClick = { selectFeature = null }) {
                    Text(
                        "Ok",
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }
        )
    }
}

fun drawLine(
    collection: List<GeoPoint>,
    map: MapView,
    color: Int,
    clicked: () -> Unit = { }
) {
    val polyline = Polygon().apply {
        fillPaint.color = Color.argb(100, 255, 30, 50)
        outlinePaint.color = color
        outlinePaint.strokeWidth = 6.0F
        setOnClickListener { _, _, _ ->
            clicked()
            false
        }
    }

    val mapPoints = collection.map { point -> GeoPoint(point.latitude, point.longitude) }
    polyline.setPoints(mapPoints)
    map.overlays.add(polyline)
}

@SuppressLint("ResourceType")
@Composable
fun rememberMapWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = 100
        }
    }
    val lifecycleObserver = rememberMapLifecycleObserver(mapView = mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_DESTROY -> mapView.onDetach()
                else -> {}
            }
        }
    }