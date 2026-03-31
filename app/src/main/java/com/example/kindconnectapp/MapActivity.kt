package com.example.kindconnectapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.search.autocomplete.PlaceAutocomplete
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var pointAnnotationManager: PointAnnotationManager? = null

    // Store destination so permission callback can access them
    private var destinationLat: Double = 0.0
    private var destinationLng: Double = 0.0
    private var destinationName: String = "Location"

    // Shows the system location permission popup
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fetchUserLocationAndDrawRoute()
        } else {
            Log.w("MapActivity", "Location permission denied — showing destination only")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapboxOptions.accessToken = getString(R.string.mapbox_search_token)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        val composeView = findViewById<ComposeView>(R.id.searchCompose)

        // NEW: initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-98.0, 39.5))
                .zoom(2.0)
                .build()
        )

        composeView.setContent { Box {} }

        // Read intent extras — same as before
        val nameExtra = intent.getStringExtra("name")
        val latVal = if (intent.extras?.containsKey("lat") == true) intent.extras?.getDouble("lat") else null
        val lngVal = if (intent.extras?.containsKey("lng") == true) intent.extras?.getDouble("lng") else null
        val addressExtra = intent.getStringExtra("address")

        lifecycleScope.launch {
            // Resolve destination coordinates — same logic as before
            if (latVal != null && lngVal != null) {
                destinationLat = latVal
                destinationLng = lngVal
                destinationName = nameExtra ?: "Location"
            } else if (!addressExtra.isNullOrBlank()) {
                val geocoded = geocodeAddress(addressExtra)
                if (geocoded != null) {
                    destinationLat = geocoded.latitude
                    destinationLng = geocoded.longitude
                    destinationName = nameExtra ?: addressExtra
                } else {
                    Log.w("MapActivity", "Geocoding failed for: $addressExtra")
                    destinationLat = 37.2279
                    destinationLng = -77.4019
                    destinationName = "Petersburg"
                }
            } else {
                destinationLat = 37.2279
                destinationLng = -77.4019
                destinationName = "Petersburg"
            }

            // Step 1: Drop the pin on the destination (same as before)
            showMarkerAndMoveCamera(destinationLat, destinationLng, destinationName)

            // Step 2: Ask for location permission then draw route
            requestLocationAndRoute()
        }

        // PlaceAutocomplete — unchanged
        val placeAutocomplete = PlaceAutocomplete.create(locationProvider = null)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                val response = placeAutocomplete.suggestions(query = "Washington DC")
                if (response.isValue) {
                    val suggestions = response.value.orEmpty()
                    Log.i("SearchExample", "Suggestions: $suggestions")
                    if (suggestions.isNotEmpty()) {
                        val result = placeAutocomplete.select(suggestions.first())
                        result.onValue { Log.i("SearchExample", "Result: $it") }
                        result.onError { Log.e("SearchExample", "Error selecting suggestion", it) }
                    }
                } else {
                    Log.e("SearchExample", "Error fetching suggestions: ${response.error}")
                }
            }
        }

        bottomNavigationView.selectedItemId = R.id.nav_resources
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_pantry -> {
                    startActivity(Intent(this, PantryActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_resources -> {
                    startActivity(Intent(this, ResourcesActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // ─── NEW: Permission check ────────────────────────────────────────────────

    private fun requestLocationAndRoute() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchUserLocationAndDrawRoute()  // already have permission
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // ─── NEW: Get user GPS location then fetch route ──────────────────────────

    private fun fetchUserLocationAndDrawRoute() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lifecycleScope.launch {
                    drawRoute(
                        originLat = location.latitude,
                        originLng = location.longitude,
                        destLat = destinationLat,
                        destLng = destinationLng
                    )
                }
            } else {
                Log.w("MapActivity", "Last known location was null")
            }
        }
    }

    // ─── NEW: Call Mapbox Directions API ─────────────────────────────────────

    private suspend fun drawRoute(
        originLat: Double, originLng: Double,
        destLat: Double, destLng: Double
    ) = withContext(Dispatchers.IO) {
        try {
            val accessToken = getString(R.string.mapbox_search_token)
            val url = "https://api.mapbox.com/directions/v5/mapbox/driving/" +
                    "$originLng,$originLat;$destLng,$destLat" +
                    "?geometries=geojson&access_token=$accessToken"

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext

            val json = JSONObject(body)
            val routes = json.getJSONArray("routes")
            if (routes.length() == 0) {
                Log.w("MapActivity", "No routes returned from Directions API")
                return@withContext
            }

            // Parse route geometry coordinates
            val geometry = routes.getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONArray("coordinates")

            val points = (0 until geometry.length()).map { i ->
                val coord = geometry.getJSONArray(i)
                Point.fromLngLat(coord.getDouble(0), coord.getDouble(1))
            }

            withContext(Dispatchers.Main) {
                renderRouteLine(points)
            }

        } catch (e: Exception) {
            Log.e("MapActivity", "Route fetch failed: ${e.localizedMessage}", e)
        }
    }

    // ─── NEW: Draw the blue route line on the map ─────────────────────────────

    private fun renderRouteLine(points: List<Point>) {
        val style = mapView.getMapboxMap().getStyle() ?: return

        val sourceId = "route-source"
        val layerId  = "route-layer"

        // Remove previous route if redrawn
        if (style.styleSourceExists(sourceId)) style.removeStyleSource(sourceId)
        if (style.styleLayerExists(layerId))   style.removeStyleLayer(layerId)

        style.addSource(
            geoJsonSource(sourceId) {
                featureCollection(
                    FeatureCollection.fromFeature(
                        Feature.fromGeometry(LineString.fromLngLats(points))
                    )
                )
            }
        )

        style.addLayer(
            lineLayer(layerId, sourceId) {
                lineColor("#1A73E8")  // blue route line
                lineWidth(5.0)
                lineCap(LineCap.ROUND)
                lineJoin(LineJoin.ROUND)
            }
        )
    }

    // show marker + camera
    private suspend fun showMarkerAndMoveCamera(lat: Double, lng: Double, title: String) {
        withContext(Dispatchers.Main) {
            try {
                val target = Point.fromLngLat(lng, lat)
                mapView.getMapboxMap().setCamera(
                    CameraOptions.Builder()
                        .center(target)
                        .zoom(15.0)
                        .build()
                )

                if (pointAnnotationManager == null) {
                    pointAnnotationManager = mapView.annotations.createPointAnnotationManager()
                }

                pointAnnotationManager?.let { manager ->
                    manager.deleteAll()

                    // 📍 Load the built-in Mapbox red pin icon as a bitmap
                    val drawable = ContextCompat.getDrawable(this@MapActivity, R.drawable.map)!!
                    val markerBitmap = Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(markerBitmap)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)

                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(target)
                        .withIconImage(markerBitmap)   // attaches the pin icon
                        .withIconSize(0.3)             // adjust for visibility
                        .withTextField(title)          // resource name label
                        .withTextSize(12.0)
                        .withTextColor(android.graphics.Color.BLACK)
                        .withTextHaloColor(android.graphics.Color.WHITE)
                        .withTextHaloWidth(1.5)
                        .withTextOffset(listOf(0.0, -3.0)) // floats label above the pin

                    manager.create(pointAnnotationOptions)
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                Log.e("MapActivity", "Error showing marker: ${e.localizedMessage}", e)
            }
        }
    }

    private suspend fun geocodeAddress(address: String) = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(this@MapActivity, Locale.getDefault())
            val results = geocoder.getFromLocationName(address, 1)
            if (!results.isNullOrEmpty()) {
                val r = results[0]
                return@withContext android.location.Location("").apply {
                    latitude = r.latitude
                    longitude = r.longitude
                }
            }
        } catch (e: Exception) {
            Log.e("MapActivity", "Geocoder failed: ${e.localizedMessage}", e)
        }
        return@withContext null
    }
}