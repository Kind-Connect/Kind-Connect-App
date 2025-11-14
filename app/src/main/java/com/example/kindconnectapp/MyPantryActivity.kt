package com.example.kindconnectapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MyPantryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pantry)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, MyPantryFragment())
            .commit()
    }
}