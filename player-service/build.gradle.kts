plugins {
    id ("com.android.library")
    id ("org.jetbrains.kotlin.android")

    //dager hilt
    id ("dagger.hilt.android.plugin")

    id ("kotlin-kapt")
}

android {
    namespace = "com.asmtunis.player_service"
    compileSdk = 33

    defaultConfig {
        minSdk = 21


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Needed MediaSessionCompat.Token
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    //dager hilt
    implementation("com.google.dagger:hilt-android:2.46")
    kapt("com.google.dagger:hilt-compiler:2.46")

    kapt ("androidx.hilt:hilt-compiler:1.0.0")



    implementation("androidx.media3:media3-ui:1.0.1")
    implementation("androidx.media3:media3-exoplayer:1.0.1")
    // For DASH playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-dash:1.0.1")
    // For HLS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-hls:1.0.1")
// For RTSP playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-rtsp:1.0.1")
// For exposing and controlling media sessions
    implementation("androidx.media3:media3-session:1.0.1")



    //COIL
    implementation("io.coil-kt:coil:2.3.0")

}

kapt {
    correctErrorTypes = true
}