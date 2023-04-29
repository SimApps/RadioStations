// Top-level build file where you can add configuration options common to all sub-projects/modules.



buildscript {
    repositories {
        google()
        mavenCentral()

    }


    dependencies {
        classpath ("com.android.tools.build:gradle:7.4.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.0.0")
        classpath ("com.google.gms:google-services:4.3.15")
       // classpath 'io.fabric.tools:gradle:1.31.0'  // Crashlytics plugin

        // To benefit from the latest Performance Monitoring plugin features,
        // update your Android Gradle Plugin dependency to at least v3.4.0

        // Add the dependency for the Performance Monitoring plugin
        classpath ("com.google.firebase:perf-plugin:1.4.2")  // Performance Monitoring plugin
        classpath ("com.google.firebase:firebase-crashlytics-gradle:2.9.5")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files

        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.6.0-alpha09")


        //Dager hilt
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.45")

        classpath ("org.jetbrains.kotlin:kotlin-serialization:1.8.10")


        // licence info
         classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")


    }



}

plugins {
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
}

allprojects {
   repositories {
        google()
        mavenCentral()

       maven { url = uri("https://jitpack.io") }
   }
}

tasks.register("clean", Delete::class) {
    delete (rootProject.buildDir)
}
