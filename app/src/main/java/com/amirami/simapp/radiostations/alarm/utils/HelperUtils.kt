package com.amirami.simapp.radiostations.alarm.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build


fun checkAboveOreo(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.O
}

fun checkAboveKitkat(): Boolean {
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT
}

fun checkAboveLollipop(): Boolean {
    return   Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP
}

