plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.google.gms.google.services)
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10"

}

android {
    namespace = "com.example.kindconnectapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.kindconnectapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }




    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.androidx.activity)
    implementation("com.mapbox.maps:android-ndk27:11.16.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.mapbox.search:autofill-ndk27:2.16.2")
    implementation("com.mapbox.search:discover-ndk27:2.16.2")
    implementation("com.mapbox.search:place-autocomplete-ndk27:2.16.2")
    implementation("com.mapbox.search:offline-ndk27:2.16.2")
    implementation("com.mapbox.search:mapbox-search-android-ndk27:2.16.2")
    implementation("com.mapbox.search:mapbox-search-android-ui-ndk27:2.16.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation(libs.androidx.constraintlayout)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.firebase.firestore)
    implementation("com.google.firebase:firebase-auth:22.1.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


}
