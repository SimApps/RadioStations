package com.amirami.simapp.radiostations

import android.content.Context
import com.amirami.simapp.radiostations.RadioFunction.unwrap
import com.google.android.play.core.review.ReviewManagerFactory

object AppRater {
    private const val DAYS_UNTIL_PROMPT = 7 // Min number of days
    private const val LAUNCHES_UNTIL_PROMPT = 1 // Min number of launches
    var launchcount = 0L
    fun applaunched(mContext: Context) {
        val prefs = mContext.getSharedPreferences("apprater", 0)
        if (prefs.getBoolean("dontshowagain", false)) {
            return
        }
        val editor = prefs.edit()
        // Increment launch counter
        launchcount = prefs.getLong("launch_count", 0) + 1
        editor.putLong("launch_count", launchcount)
        // Get date of first launch
        var datefirstLaunch = prefs.getLong("date_firstlaunch", 0)
        if (datefirstLaunch == 0L) {
            datefirstLaunch = System.currentTimeMillis()
            editor.putLong("date_firstlaunch", datefirstLaunch)
        }
        // Wait at least n days before opening
        if (launchcount >= LAUNCHES_UNTIL_PROMPT) {
            if ((System.currentTimeMillis() >= (datefirstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)))) {
                nativeRate(mContext)
            }
        }
        editor.apply()
    }

    private fun nativeRate(mContext: Context) {
        val manager = ReviewManagerFactory.create(mContext)

        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result

                val flow = manager.launchReviewFlow(unwrap(mContext), reviewInfo)
                flow.addOnCompleteListener {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            } /* else {
                //   DynamicToast.makeSuccess(requireContext(),"eere",9)
                // There was some problem, log or handle the error code.
                //   @ReviewErrorCode val reviewErrorCode = (task.exception as TaskException).errorCode
            }*/
        }
    }
}
