package com.example.kindconnectapp




//Justin
//import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.zIndex
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity // Use AppCompatActivity for Material Components
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.example.androidlocationsearch.SearchScreen
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.common.MapboxOptions
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

import com.mapbox.search.result.SearchResult
class MapActivity : ComponentActivity() {
    private lateinit var mapView: MapView
    // You can also declare a variable for the bottom navigation view
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapboxOptions.accessToken = getString(R.string.mapbox_search_token)
        val placeAutocomplete = PlaceAutocomplete.create(locationProvider = null)

        // --- THIS IS THE KEY CHANGE ---
        // Set the content view to your XML layout file instead of just the MapView
        setContentView(R.layout.activity_map)

        // Now, find the views by their IDs defined in the XML file
        mapView = findViewById(R.id.mapView)
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        // Initialize the map (this code can stay)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-98.0, 39.5))
                .pitch(0.0)
                .zoom(2.0)
                .bearing(0.0)
                .build()


        )
        val composeView = findViewById<ComposeView>(R.id.searchCompose)
        composeView.setContent {
            val selectedResult = remember { mutableStateOf<SearchResult?>(null) }
            Box {
                SearchScreen(
                    modifier = Modifier
                        .zIndex(1f)
                        .align(Alignment.TopCenter)
                )
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                val response = placeAutocomplete.suggestions(query = "Washington DC")

                if (response.isValue) {
                    val suggestions = response.value.orEmpty()
                    Log.i("SearchExample", "Suggestions: $suggestions")

                    if (suggestions.isNotEmpty()) {
                        val result = placeAutocomplete.select(suggestions.first())

                        result.onValue {
                            Log.i("SearchExample", "Result: $it")
                        }

                        result.onError {
                            Log.e("SearchExample", "Error selecting suggestion", it)
                        }
                    }
                } else {
                    Log.e("SearchExample", "Error fetching suggestions: ${response.error}")
                }
            }
        }

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottom.selectedItemId = R.id.nav_map  // highlight Pantry tab

        // Optional: Set up a listener for your navigation bar
        bottom.setOnItemSelectedListener { item ->
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
                }  // already here
                R.id.nav_map -> true
                R.id.nav_resources -> {
                    startActivity(Intent(this, ResourcesActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }



    /*
    // Remember to add lifecycle methods for the MapView
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }*/
}
