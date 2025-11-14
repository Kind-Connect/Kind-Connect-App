package com.example.kindconnectapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PantryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantry)

        findViewById<Button>(R.id.goPantryButton).setOnClickListener {
            val intent = Intent(this, MyPantryActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.goRgButton).setOnClickListener {
            val intent = Intent(this, RecipeGeneratorActivity::class.java)
            startActivity(intent)
        }



    }
}
