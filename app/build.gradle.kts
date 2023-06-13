plugins {
    id ("com.android.application")
    id ("kotlin-android")

    id ("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id ("kotlin-kapt")


    id ("kotlin-parcelize")
// Add the Firebase Crashlytics plugin.
    id ("com.google.firebase.crashlytics")
    id ("com.google.gms.google-services")
    id ("com.google.firebase.firebase-perf")

    id ("androidx.navigation.safeargs.kotlin")

    //dager hilt
    id ("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.android")


    id("com.google.android.gms.oss-licenses-plugin")
}
android {
    compileSdk = 33
    defaultConfig {
        applicationId = "com.amirami.simapp.radiobroadcast"

        minSdk = 24//21 ALARM LIB
        targetSdk = 33
        versionCode = 4
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding =  true
        dataBinding=  true
    }
    namespace =  "com.amirami.simapp.radiobroadcast"
}

dependencies {
    //implementation fileTree(dir: "libs", include: ["*.jar"])

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    //implementation (project(path:(":player-service"))

    implementation(project(":player-service"))
    //implementation project(path:(":downloader"))
    implementation(project(":downloader"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
    implementation("androidx.hilt:hilt-work:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // kotlin serialization
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")


    //reetrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //splash screan
    implementation("androidx.core:core-splashscreen:1.0.1")
    //in app review
    implementation("com.google.android.play:core-ktx:1.8.1")
    //autoUpdate
 //   implementation("com.google.android.play:app-update-ktx:2.0.0")

    //COIL
    implementation("io.coil-kt:coil:2.4.0")
    implementation("io.coil-kt:coil-gif:2.4.0")
    implementation("io.coil-kt:coil-svg:2.4.0")


    implementation("androidx.media3:media3-ui:1.0.2")
    implementation("androidx.media3:media3-exoplayer:1.0.2")
    // For DASH playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-dash:1.0.2")
    // For HLS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-hls:1.0.2")
// For RTSP playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-rtsp:1.0.2")
// For exposing and controlling media sessions
    implementation("androidx.media3:media3-session:1.0.2")





    //tappx
    implementation("com.tappx.sdk.android:tappx-sdk:4.0.4")
    implementation("com.google.android.gms:play-services-base:18.2.0")


    // Import the BoM for the Firebase platform
    implementation (platform("com.google.firebase:firebase-bom:32.1.0"))

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
    implementation("com.google.android.gms:play-services-ads:22.1.0")


    // When using the BoM, you don")t specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-appcheck-playintegrity")


    //custom toast
    implementation("com.pranavpandey.android:dynamic-toasts:4.1.3")

    //RecyclerView-FastScroll
    implementation("com.simplecityapps:recyclerview-fastscroll:2.0.1")

    //leakcanary memory leak
    // debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")


    //download library
   // implementation("com.github.alirezat775:downloader:1.0.2")



    // this line below because a wierd warning about viewbinding
    compileOnly("com.android.databinding:viewbinding:8.0.2")

// Navigation Component dependencies
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.0-alpha01")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.0-alpha01")

    // Coroutine Lifecycle Scopes
    implementation("androidx.fragment:fragment-ktx:1.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
   // implementation("androidx.lifecycle:lifecycle-extensions-ktx:2.5.1")


    //dager hilt
    implementation("com.google.dagger:hilt-android:2.46.1")
    kapt("com.google.dagger:hilt-compiler:2.46.1")

    kapt("androidx.hilt:hilt-compiler:1.0.0")
    





    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //kotlin caroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")


    // - - Room Persistence Library
    implementation("androidx.room:room-runtime:2.5.1")
    ksp("androidx.room:room-compiler:2.5.1")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.5.1")



    implementation("com.airbnb.android:lottie:6.0.1")




    //size library
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("me.jfenn:SlideActionView:0.0.2")

    implementation ("com.github.Cutta:GifView:1.4")


    implementation ("com.github.lisawray.groupie:groupie:2.10.1")
    implementation ("com.github.lisawray.groupie:groupie-viewbinding:2.10.1")
    implementation ("com.github.lisawray.groupie:groupie-databinding:2.10.1")



    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    implementation ("androidx.viewpager2:viewpager2:1.1.0-beta02")
    implementation ("io.github.nikartm:fit-button:2.0.0")
    implementation ("com.github.YvesCheung.RollingText:RollingText:1.2.11")

   // implementation(files("libs/customFloating-release.aar"))
    implementation("com.robertlevonyan.view:CustomFloatingActionButton:3.1.5")

    // ALARM LIB
    //implementation ("com.github.ColdTea-Projects:SmplrAlarm:2.1.1")
  //  implementation ("com.carterchen247:alarm-scheduler:2.0.0") CURRENT ONE

// GET LICENSE INFO
    implementation("com.google.android.gms:play-services-oss-licenses:17.0.1")


    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.22-1.0.11")


}

kapt {
    correctErrorTypes = true
}



