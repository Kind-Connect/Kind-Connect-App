package com.example.kindconnectapp




//Justin
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity // Use AppCompatActivity for Material Components
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-98.0, 39.5))
                .pitch(0.0)
                .zoom(2.0)
                .bearing(0.0)
                .build()
        )

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottom.selectedItemId = R.id.nav_map

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
                }
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
