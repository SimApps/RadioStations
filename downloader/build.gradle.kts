plugins {
    id ("com.android.library")
    id ("kotlin-android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.android")
    //  id 'kotlin-android-extensions'
}

android {
    namespace = "com.amirami.simapp.downloader"
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
   // implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")



    implementation("androidx.room:room-runtime:2.5.1")
    ksp("androidx.room:room-compiler:2.5.1")


    //kotlin caroutine
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.1")

}

