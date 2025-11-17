package com.example.kindconnectapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val signInBtn = findViewById<Button>(R.id.signInButton)
        val registerBtn = findViewById<Button>(R.id.signUpButton)

        // SharedPreferences for saving login info
        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        fun validate(): Boolean {
            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()

            // Email must not be empty
            if (email.isEmpty()) {
                emailInput.error = "Email required"
                return false
            }

            // Email must be valid format
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Enter a valid email"
                return false
            }

            // Password required
            if (pass.isEmpty()) {
                passwordInput.error = "Password required"
                return false
            }

            // Password must be 8–12 characters
            if (pass.length < 8 || pass.length > 12) {
                passwordInput.error = "Password must be 8–12 characters"
                return false
            }

            return true
        }

        // REGISTER
        registerBtn.setOnClickListener {
            if (!validate()) return@setOnClickListener

            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()

            // Save email/password locally
            prefs.edit()
                .putString("email", email)
                .putString("password", pass)
                .apply()

            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, HomePage::class.java))
            finish()
        }

        // SIGN IN
        signInBtn.setOnClickListener {
            if (!validate()) return@setOnClickListener

            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()

            val savedEmail = prefs.getString("email", null)
            val savedPass = prefs.getString("password", null)

            if (email == savedEmail && pass == savedPass) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomePage::class.java))
                finish()
            } else {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}