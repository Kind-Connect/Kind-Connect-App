package com.example.kindconnectapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RecipeGeneratorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_generator)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, RecipeFragment())
            .commit()
    }
}