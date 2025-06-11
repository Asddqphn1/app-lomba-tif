plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.lombatif"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lombatif"
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.webkit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    val lastVersion = "2.11.0"
    implementation ("com.squareup.retrofit2:retrofit:${lastVersion}")
    implementation ("com.squareup.retrofit2:converter-gson:${lastVersion}")
    implementation ("androidx.compose.material:material:1.6.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("androidx.compose.material3:material3:1.2.0") // atau versi terbaru
    // Tambahkan di build.gradle (Module)
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.10.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.33.2-alpha")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("io.github.vanpra.compose-material-dialogs:core:0.8.1-rc")
    implementation ("io.github.vanpra.compose-material-dialogs:datetime:0.8.1-rc")

}