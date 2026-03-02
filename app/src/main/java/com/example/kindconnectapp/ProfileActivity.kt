package com.example.kindconnectapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var signInBtn: MaterialButton
    private lateinit var registerBtn: MaterialButton

    private val PREFS = "UserPrefs"
    private val ACCOUNTS_KEY = "accounts_json"
    private val CURRENT_KEY = "current_user_email"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        signInBtn = findViewById(R.id.signInButton)
        registerBtn = findViewById(R.id.signUpButton)

        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        // Auto-login if session exists
        val currentUser = prefs.getString(CURRENT_KEY, null)
        if (!currentUser.isNullOrEmpty()) {
            startActivity(Intent(this, HomePage::class.java))
            finish()
            return
        }

        fun loadAccounts(): JSONObject {
            val raw = prefs.getString(ACCOUNTS_KEY, null)
            return if (raw.isNullOrEmpty()) JSONObject()
            else try { JSONObject(raw) }
            catch (e: JSONException) { JSONObject() }
        }

        fun saveAccounts(obj: JSONObject) {
            prefs.edit().putString(ACCOUNTS_KEY, obj.toString()).apply()
        }

        // ---------------- REGISTER ----------------
        registerBtn.setOnClickListener {

            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim().lowercase()
            val pass = passwordInput.text.toString().trim()

            if (name.isEmpty()) {
                nameInput.error = "Enter full name"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Enter valid email"
                return@setOnClickListener
            }

            if (pass.length !in 8..12) {
                passwordInput.error = "Password must be 8–12 characters"
                return@setOnClickListener
            }

            val accounts = loadAccounts()

            if (accounts.has(email)) {
                Toast.makeText(this, "Account already exists. Please login.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val accountObj = JSONObject().apply {
                put("name", name)
                put("password", pass)
            }

            accounts.put(email, accountObj)
            saveAccounts(accounts)

            prefs.edit().putString(CURRENT_KEY, email).apply()

            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomePage::class.java))
            finish()
        }

        // ---------------- LOGIN ----------------
        signInBtn.setOnClickListener {

            val email = emailInput.text.toString().trim().lowercase()
            val pass = passwordInput.text.toString().trim()

            val accounts = loadAccounts()

            if (!accounts.has(email)) {
                Toast.makeText(this, "No account found. Please register.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val accountObj = accounts.getJSONObject(email)
            val savedPass = accountObj.optString("password")

            if (savedPass == pass) {

                prefs.edit().putString(CURRENT_KEY, email).apply()

                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomePage::class.java))
                finish()

            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}