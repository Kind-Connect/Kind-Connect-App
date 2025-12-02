package com.example.kindconnectapp

data class Recipe(
    val id: Int = 0,
    val title: String = "",
    val image: String = "",
    val usedIngredientCount: Int? = null,
    val missedIngredientCount: Int? = null,
    val summary: String? = null,
    val instructions: String? = null,
    var isFavorite: Boolean = false
)
