package com.example.kindconnectapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
    private val ACCOUNTS_KEY = "accounts_json" // JSON mapping: email -> { name, password }
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


        fun loadAccounts(): JSONObject {
            val raw = prefs.getString(ACCOUNTS_KEY, null)
            return if (raw.isNullOrEmpty()) JSONObject() else try {
                JSONObject(raw)
            } catch (e: JSONException) {
                JSONObject()
            }
        }

        fun saveAccounts(obj: JSONObject) {
            prefs.edit().putString(ACCOUNTS_KEY, obj.toString()).apply()
        }

        // Validation for registration
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

        // --- Register new account (multiple accounts supported) ---
        registerBtn.setOnClickListener {
            if (!validateRegister()) return@setOnClickListener

            val name = nameInput.text.toString().trim()
            val emailKey = emailInput.text.toString().trim().lowercase() // normalize
            val pass = passwordInput.text.toString().trim()

            val accounts = loadAccounts()

            if (accounts.has(emailKey)) {
                Toast.makeText(this, "Email already registered. Please sign in.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val accountObj = JSONObject()
            accountObj.put("name", name)
            accountObj.put("password", pass)

            accounts.put(emailKey, accountObj)
            saveAccounts(accounts)

            // set this as current signed-in account
            prefs.edit().putString(CURRENT_KEY, emailKey).apply()

            Toast.makeText(this, "Registration successful — signed in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomePage::class.java))
            finish()
        }

        // Sign in to an existing account
        signInBtn.setOnClickListener {
            val emailKey = emailInput.text.toString().trim().lowercase()
            val pass = passwordInput.text.toString().trim()
            val typedName = nameInput.text.toString().trim()

            val accounts = loadAccounts()

            if (!accounts.has(emailKey)) {
                Toast.makeText(this, "No account found for this email. Please register first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val accountObj = try {
                accounts.getJSONObject(emailKey)
            } catch (e: JSONException) {
                Toast.makeText(this, "Account data corrupted. Please register again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val savedPass = accountObj.optString("password", null)
            val savedName = accountObj.optString("name", "")

            if (savedPass != null && savedPass == pass) {
                // Update stored name if typed a new one while signing in
                if (typedName.isNotEmpty() && typedName != savedName) {
                    accountObj.put("name", typedName)
                    accounts.put(emailKey, accountObj)
                    saveAccounts(accounts)
                }

                prefs.edit().putString(CURRENT_KEY, emailKey).apply()

                Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomePage::class.java))
                finish()
            } else {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
            }
        }


        val lastEmail = prefs.getString(CURRENT_KEY, null)
        if (!lastEmail.isNullOrEmpty()) {
            emailInput.setText(lastEmail)
        }
    }
}