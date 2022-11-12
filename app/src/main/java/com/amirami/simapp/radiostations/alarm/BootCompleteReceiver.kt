package com.amirami.simapp.radiostations.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED) ||
            intent?.action == "android.intent.action.BOOT_COMPLETED" ||
            intent?.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            if (androidx.preference.PreferenceManager.getDefaultSharedPreferences(context!!).getLong("timeInMilli", 1L) != 1L) {
                Utils.setAlarm(
                    context,
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong("timeInMilli", 1L)
                )
            }
        }
        /*
        if (intent?.action == "android.intent.action.BOOT_COMPLETED"
        || intent?.action == "android.intent.action.QUICKBOOT_POWERON") {

            // ideally we should be fetching the data from a database
          //  val sharedPref = context?.getSharedPreferences("MyPref", Context.MODE_PRIVATE) ?: return
          //  val timeInMilli = sharedPref.getLong("timeInMilli", 1)




            Utils.setAlarm(context!!,
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(context).getLong("timeInMilli", 1))

         //   Toast.makeText(context,"BootCompleteReceiver  $timeInMilli " , Toast.LENGTH_SHORT).show()

        }
   */
    }
}
