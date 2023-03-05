package com.asmtunis.alarm.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}




