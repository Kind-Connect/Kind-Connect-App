package com.example.kindconnectapp

import android.content.Context

object FavoritesStore {
    private const val PREFS = "UserPrefs"
    private const val KEY_FAV_RECIPES = "fav_recipes"
    private const val KEY_FAV_SHELTERS = "fav_shelters"

    fun addRecipe(context: Context, recipeName: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_FAV_RECIPES, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        set.add(recipeName.trim())
        prefs.edit().putStringSet(KEY_FAV_RECIPES, set).apply()
    }

    fun addShelter(context: Context, shelterName: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_FAV_SHELTERS, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        set.add(shelterName.trim())
        prefs.edit().putStringSet(KEY_FAV_SHELTERS, set).apply()
    }

    fun getRecipes(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return (prefs.getStringSet(KEY_FAV_RECIPES, emptySet()) ?: emptySet()).toList().sorted()
    }

    fun getShelters(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return (prefs.getStringSet(KEY_FAV_SHELTERS, emptySet()) ?: emptySet()).toList().sorted()
    }
}