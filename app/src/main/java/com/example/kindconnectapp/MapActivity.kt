package com.example.kindconnectapp
//Comment for commit
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.search.autocomplete.PlaceAutocomplete
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlinx.coroutines.CancellationException
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlin.jvm.java

class MapActivity : AppCompatActivity() {

    // CLASS-LEVEL fields — required so lifecycle methods can access them
    private lateinit var mapView: MapView
    private lateinit var bottomNavigationView: BottomNavigationView
    private var pointAnnotationManager: PointAnnotationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapboxOptions.accessToken = getString(R.string.mapbox_search_token)
        setContentView(R.layout.activity_map)

        // IMPORTANT: initialize the class-level properties (do NOT re-declare with `val` here)
        mapView = findViewById(R.id.mapView)
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        val composeView = findViewById<ComposeView>(R.id.searchCompose)

        // default camera while waiting for intent
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-98.0, 39.5))
                .zoom(2.0)
                .build()
        )

        composeView.setContent { Box {} }

        // Read intent extras
        val nameExtra = intent.getStringExtra("name")
        val latVal = if (intent.extras?.containsKey("lat") == true) intent.extras?.getDouble("lat") else null
        val lngVal = if (intent.extras?.containsKey("lng") == true) intent.extras?.getDouble("lng") else null
        val addressExtra = intent.getStringExtra("address")

        lifecycleScope.launch {
            if (latVal != null && lngVal != null) {
                showMarkerAndMoveCamera(latVal, lngVal, nameExtra ?: "Location")
            } else if (!addressExtra.isNullOrBlank()) {
                val geocoded = geocodeAddress(addressExtra)
                if (geocoded != null) {
                    showMarkerAndMoveCamera(geocoded.latitude, geocoded.longitude, nameExtra ?: addressExtra)
                } else {
                    Log.w("MapActivity", "Geocoding failed for address: $addressExtra")
                    showMarkerAndMoveCamera(37.2279, -77.4019, "Petersburg")
                }
            } else {
                showMarkerAndMoveCamera(37.2279, -77.4019, "Petersburg")
            }
        }

        // PlaceAutocomplete example
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
                    startActivity(android.content.Intent(this, HomePage::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_pantry -> {
                    startActivity(android.content.Intent(this, PantryActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_profile -> {
                    startActivity(android.content.Intent(this, ProfilePageActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_resources -> {
                    startActivity(android.content.Intent(this, ResourcesActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    } // <-- ensure this closing brace is present


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // don't call mapView.onSaveInstanceState(outState) — protected in View
    }

    // show marker + camera
    private suspend fun showMarkerAndMoveCamera(lat: Double, lng: Double, title: String) {
        withContext(Dispatchers.Main) {
            try {
                val target = Point.fromLngLat(lng, lat)
                mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(target).zoom(15.0).build())

                if (pointAnnotationManager == null) {
                    pointAnnotationManager = mapView.annotations.createPointAnnotationManager()
                }

                pointAnnotationManager?.let { manager ->
                    manager.deleteAll()
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(target)
                        .withTextField(title)
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