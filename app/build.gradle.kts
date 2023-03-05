plugins {
    id ("com.android.application")
    id ("kotlin-android")

    id ("kotlin-parcelize")
    id ("kotlin-kapt")
// Add the Firebase Crashlytics plugin.
    id ("com.google.firebase.crashlytics")
    id ("com.google.gms.google-services")
    id ("com.google.firebase.firebase-perf")

    id ("androidx.navigation.safeargs.kotlin")

    //dager hilt
    id ("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.android")
}
android {
    compileSdk = 33
    defaultConfig {
        applicationId = "com.amirami.simapp.radiostations"

        minSdk = 24//21 ALARM LIB
        targetSdk = 33
        versionCode = 65
        versionName = "@string/version"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        getByName("release") {

            isMinifyEnabled =  false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding =  true
        dataBinding=  true
    }
    namespace =  "com.amirami.simapp.radiostations"
}

dependencies {
    //implementation fileTree(dir: "libs", include: ["*.jar"])

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.activity:activity-ktx:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    //implementation (project(path:(":player-service"))

    implementation(project(":player-service"))
    //implementation project(path:(":downloader"))
    implementation(project(":downloader"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //reetrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //splash screan
    implementation("androidx.core:core-splashscreen:1.0.0")
    //in app review
    implementation("com.google.android.play:core-ktx:1.8.1")
    //autoUpdate
 //   implementation("com.google.android.play:app-update-ktx:2.0.0")

    //COIL
    implementation("io.coil-kt:coil:2.2.2")
    implementation("io.coil-kt:coil-gif:2.2.2")
    implementation("io.coil-kt:coil-svg:2.2.2")


    implementation("androidx.media3:media3-ui:1.0.0-rc01")
    implementation("androidx.media3:media3-exoplayer:1.0.0-rc01")
    // For DASH playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-dash:1.0.0-rc01")
    // For HLS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-hls:1.0.0-rc01")
// For RTSP playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-rtsp:1.0.0-rc01")
// For exposing and controlling media sessions
    implementation("androidx.media3:media3-session:1.0.0-rc01")


    //key board event
    implementation("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:2.3.0")


    //tappx
    implementation("com.tappx.sdk.android:tappx-sdk:4.0.2")
    implementation("com.google.android.gms:play-services-base:18.2.0")


    // Import the BoM for the Firebase platform
    implementation (platform("com.google.firebase:firebase-bom:31.2.2"))

    // Add the Firebase Crashlytics SDK.
    implementation("com.google.firebase:firebase-crashlytics-ktx")


    // Declare the KTX library instead (which automatically has a dependency on the base library)
    implementation("com.google.firebase:firebase-analytics-ktx")


// Add the dependency for the Performance Monitoring library
    implementation("com.google.firebase:firebase-perf-ktx")

//firebase auth
   // implementation("com.google.firebase:firebase-auth-ktx")
     implementation("com.firebaseui:firebase-ui-auth:8.0.2")

    //firebase firestore
    implementation("com.google.firebase:firebase-firestore-ktx")


    //ads to remove in pro
    implementation("com.google.android.gms:play-services-ads:21.5.0")


    // When using the BoM, you don")t specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-appcheck-playintegrity")


    //custom toast
    implementation("com.pranavpandey.android:dynamic-toasts:4.1.2")

    //RecyclerView-FastScroll
    implementation("com.simplecityapps:recyclerview-fastscroll:2.0.1")

    //leakcanary memory leak
    // debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")


    //download library
   // implementation("com.github.alirezat775:downloader:1.0.2")



    // this line below because a wierd warning about viewbinding
    compileOnly("com.android.databinding:viewbinding:7.4.1")

// Navigation Component dependencies
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0-alpha06")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0-alpha06")

    // Coroutine Lifecycle Scopes
    implementation("androidx.fragment:fragment-ktx:1.5.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
   // implementation("androidx.lifecycle:lifecycle-extensions-ktx:2.5.1")


    //dager hilt
    implementation("com.google.dagger:hilt-android:2.45")
    kapt("com.google.dagger:hilt-compiler:2.45")

    kapt("androidx.hilt:hilt-compiler:1.0.0")
    





    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //kotlin caroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")


    // - - Room Persistence Library
    implementation("androidx.room:room-runtime:2.5.0")
    kapt("androidx.room:room-compiler:2.5.0")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.5.0")


   // def lottieVersion =(")5.0.3")
    implementation("com.airbnb.android:lottie:6.0.0")





    //size library
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("me.jfenn:SlideActionView:0.0.2")

    implementation ("com.github.Cutta:GifView:1.4")


    // ALARM LIB
    implementation ("com.github.ColdTea-Projects:SmplrAlarm:2.1.1")
}

kapt {
    correctErrorTypes = true
}



