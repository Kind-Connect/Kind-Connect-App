package com.example.kindconnectapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val signInBtn = findViewById<MaterialButton>(R.id.signInButton)
        val registerBtn = findViewById<MaterialButton>(R.id.signUpButton)

        val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        fun validateRegister(): Boolean {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()

            if (name.isEmpty()) {
                nameInput.error = "Enter name"
                return false
            }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Enter valid email"
                return false
            }
            if (pass.isEmpty() || pass.length !in 8..12) {
                passwordInput.error = "8–12 character password"
                return false
            }
            return true
        }

        registerBtn.setOnClickListener {
            if (!validateRegister()) return@setOnClickListener

            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()

            prefs.edit()
                .putString("name", name)
                .putString("email", email)
                .putString("password", pass)
                .apply()

            startActivity(Intent(this, HomePage::class.java))
            finish()
        }

        signInBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val pass = passwordInput.text.toString().trim()
            val typedName = nameInput.text.toString().trim()

            val savedEmail = prefs.getString("email", null)
            val savedPass = prefs.getString("password", null)

            if (email == savedEmail && pass == savedPass) {
                if (typedName.isNotEmpty()) {
                    prefs.edit().putString("name", typedName).apply()
                }
                startActivity(Intent(this, HomePage::class.java))
                finish()
            } else {
                Toast.makeText(this, "Incorrect login", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
