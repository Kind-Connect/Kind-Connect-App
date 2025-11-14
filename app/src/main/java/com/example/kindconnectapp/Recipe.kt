package com.example.kindconnectapp

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int,
    val summary: String? = null,
    val instructions: String? = null,
    var isFavorite: Boolean = false
)