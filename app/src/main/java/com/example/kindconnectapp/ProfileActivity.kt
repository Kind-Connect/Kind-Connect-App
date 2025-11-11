package com.example.kindconnectapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userId = findViewById<EditText>(R.id.userIDInput)
        val password = findViewById<EditText>(R.id.passwordInput)
        val signIn = findViewById<Button>(R.id.signInButton)
        val signUp = findViewById<Button>(R.id.signUpButton)

        val goHome: () -> Unit = {
            Toast.makeText(this, "You have been signed in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomePage::class.java))
            finish() // don’t return to login on back
        }

        fun validate(): Boolean {
            val u = userId.text?.toString()?.trim().orEmpty()
            val p = password.text?.toString()?.trim().orEmpty()
            if (u.isEmpty()) { userId.error = "User ID required"; return false }
            if (p.isEmpty()) { password.error = "Password required"; return false }
            return true
        }

        signIn.setOnClickListener { if (validate()) goHome() }
        signUp.setOnClickListener { if (validate()) goHome() }
    }
}
