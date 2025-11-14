package com.example.kindconnectapp

import android.os.Bundle
import android.text.Html
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
        instructionsView.text = Html.fromHtml(instructions ?: "No instructions available", Html.FROM_HTML_MODE_LEGACY)
        Glide.with(this).load(imageUrl).into(imageView)
    }
}
