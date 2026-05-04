package com.example.kindconnectapp

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class RecipeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val title = intent.getStringExtra("title")
        val instructions = intent.getStringExtra("instructions")
        val imageUrl = intent.getStringExtra("image")

        val titleView = findViewById<TextView>(R.id.recipeTitle)
        val instructionsView = findViewById<TextView>(R.id.recipeInstructions)
        val imageView = findViewById<ImageView>(R.id.recipeImage)

        titleView.text = title
        Glide.with(this).load(imageUrl).into(imageView)
        val raw = instructions ?: ""

        val noHtml = Html.fromHtml(raw, Html.FROM_HTML_MODE_LEGACY).toString()

        val steps = noHtml.split(".")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val formatted = steps.mapIndexed { index, step ->
            "${index + 1}. $step"
        }.joinToString("\n\n")

        instructionsView.text = formatted


        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.navigationIcon?.setTint(Color.WHITE)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
