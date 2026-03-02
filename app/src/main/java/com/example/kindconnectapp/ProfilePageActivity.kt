package com.example.kindconnectapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject

class ProfilePageActivity : AppCompatActivity() {

    private val PREFS = "UserPrefs"
    private val ACCOUNTS_KEY = "accounts_json"
    private val CURRENT_KEY = "current_user_email"
    private val IMAGE_PICK_CODE = 1001

    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileSubtitle: TextView
    private lateinit var favoriteCount: TextView
    private lateinit var mealsMade: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        profileImage = findViewById(R.id.profileImage)
        profileName = findViewById(R.id.profileName)
        profileSubtitle = findViewById(R.id.profileSubtitle)
        favoriteCount = findViewById(R.id.favoriteCount)
        mealsMade = findViewById(R.id.mealsMade)

        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val currentEmail = prefs.getString(CURRENT_KEY, null)
        val raw = prefs.getString(ACCOUNTS_KEY, "{}")

        if (currentEmail != null) {
            val accounts = JSONObject(raw)
            if (accounts.has(currentEmail)) {
                val userObj = accounts.getJSONObject(currentEmail)
                val name = userObj.getString("name")
                profileName.text = "Hi $name"
                profileSubtitle.text = currentEmail
            }

            val savedImageUri =
                prefs.getString("profile_image_uri_$currentEmail", null)

            if (savedImageUri != null) {
                profileImage.setImageURI(Uri.parse(savedImageUri))
            }
        }

        profileImage.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottom.selectedItemId = R.id.nav_profile

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_pantry -> {
                    startActivity(Intent(this, PantryActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_resources -> {
                    startActivity(Intent(this, ResourcesActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_profile -> true

                else -> false
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE &&
            resultCode == Activity.RESULT_OK &&
            data != null
        ) {
            val imageUri: Uri? = data.data

            if (imageUri != null) {
                profileImage.setImageURI(imageUri)

                val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                val currentEmail =
                    prefs.getString(CURRENT_KEY, null)

                if (currentEmail != null) {
                    prefs.edit()
                        .putString(
                            "profile_image_uri_$currentEmail",
                            imageUri.toString()
                        )
                        .apply()
                }
            }
        }
    }
}