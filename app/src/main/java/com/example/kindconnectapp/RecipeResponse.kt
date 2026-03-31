package com.example.kindconnectapp

data class RecipeResponse(
    val id: Int,
    val title: String,
    val image: String,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int
)