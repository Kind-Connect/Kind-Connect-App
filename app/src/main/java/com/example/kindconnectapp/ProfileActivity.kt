package com.example.kindconnectapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userIDInput = findViewById<EditText>(R.id.userIDInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val signInButton = findViewById<Button>(R.id.signInButton)

        signInButton.setOnClickListener {
            val userID = userIDInput.text.toString()
            val password = passwordInput.text.toString()

            if (userID.isEmpty() && password.isEmpty()) {
                Toast.makeText(this, "Enter both fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Signed in as $userID", Toast.LENGTH_SHORT).show()

            }

        }
    }

}