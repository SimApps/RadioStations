package com.amirami.simapp.radiostations.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class TimeChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == "android.intent.action.TIME_SET") {
            // ideally we should be fetching the data from a database
         //   val sharedPref = context?.getSharedPreferences("MyPref",Context.MODE_PRIVATE) ?: return
         //   val timeInMilli = sharedPref.getLong("timeInMilli", 1)

            val timeInMilli =   androidx.preference.PreferenceManager.getDefaultSharedPreferences(context!!).getLong("timeInMilli", 1)

            Utils.setAlarm(context, timeInMilli)

           // Toast.makeText(context,"TimeChangedReceiver  $timeInMilli " , Toast.LENGTH_SHORT).show()

        }
    }
}