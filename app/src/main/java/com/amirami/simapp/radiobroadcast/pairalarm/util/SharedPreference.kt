package com.amirami.simapp.radiobroadcast.pairalarm.util

import android.content.Context
import java.util.*

class SharedPreference(context: Context) {
    private val prefs = context.getSharedPreferences(TEST, Context.MODE_PRIVATE)

    fun putStringData(previousString: String, additionalText: String? = "") {
        val currentMillis = Calendar.getInstance().timeInMillis
        prefs.edit().apply {
            putString(TEST_TEXT_CURRENTTIME, previousString + "\n ${transMillisToTime(currentMillis)}  $additionalText")
            apply()
        }

    }

    fun getStringData() = prefs.getString(TEST_TEXT_CURRENTTIME, "") ?: ""
}