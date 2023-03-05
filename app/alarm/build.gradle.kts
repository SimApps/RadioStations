plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
}

android {
    namespace = "com.asmtunis.alarm"
    compileSdk = 33

    defaultConfig {
        minSdk = 21

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // ViewModel
    implementation("androidx.fragment:fragment-ktx:1.5.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    // implementation("androidx.lifecycle:lifecycle-extensions-ktx:2.5.1")


    //dager hilt
    implementation("com.google.dagger:hilt-android:2.44.2")
    kapt("com.google.dagger:hilt-compiler:2.44.2")

    kapt("androidx.hilt:hilt-compiler:1.0.0")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //kotlin caroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")


    // - - Room Persistence Library
    implementation("androidx.room:room-runtime:2.5.0")
    kapt("androidx.room:room-compiler:2.5.0")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.5.0")
}

kapt {
    correctErrorTypes = true
}