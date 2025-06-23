plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.mmarquezroldan.horsegame"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mmarquezroldan.horsegame"
        minSdk = 24
        targetSdk = 35
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
    // REMOVE THIS LINE: It's the old Material Design 2 library and conflicts with Material3
    // implementation(libs.material)

    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.ads.api)

    // Keep this for Material3. Ensure libs.androidx.material3.android points to your desired version (e.g., 1.3.2)
    implementation(libs.androidx.material3.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Stripe Android SDK (already fixed)
    implementation(libs.stripe.android)
    implementation(libs.financial.connections)

//    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui:1.5.0")
    // REMOVE THIS LINE: This is Compose Material (Material Design 2) and conflicts with Material3
    // implementation("androidx.compose.material:material:1.5.0")

    // ADD THIS for Compose Material3 components if you are using Jetpack Compose UI
    implementation("androidx.compose.material3:material3:1.3.2") // Or use libs.compose.material3 if defined in libs.versions.toml

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}