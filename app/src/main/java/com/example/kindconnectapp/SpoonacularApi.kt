package com.example.kindconnectapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularService {

    @GET("recipes/findByIngredients")
    suspend fun getRecipesByIngredients(
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int,
        @Query("apiKey") apiKey: String
    ): List<RecipeResponse>

    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): Recipe
}

object SpoonacularApi {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService: SpoonacularService by lazy {
        retrofit.create(SpoonacularService::class.java)
    }
}