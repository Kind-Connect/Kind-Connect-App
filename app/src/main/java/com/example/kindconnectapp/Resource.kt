package com.example.kindconnectapp

data class Resource(
    val name: String,
    val description: String,
    val urlOrAddress: String,
    val category: String,
    val lat: Double? = null,
    val lng: Double? = null,
    val imageRes: Int? = null
)



