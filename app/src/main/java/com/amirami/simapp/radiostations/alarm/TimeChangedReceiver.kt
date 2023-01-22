package com.amirami.simapp.radiostations.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amirami.simapp.radiostations.data.datastore.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimeChangedReceiver @Inject constructor(

    var dataViewModel: DataViewModel
): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.TIME_SET") {
            // ideally we should be fetching the data from a database
            //   val sharedPref = context?.getSharedPreferences("MyPref",Context.MODE_PRIVATE) ?: return
            //   val timeInMilli = sharedPref.getLong("timeInMilli", 1)

            val timeInMilli = dataViewModel.getTimeInMillis().toLong().toLong()

            if (context != null) {
                Utils.setAlarm(context, timeInMilli)
            }

            // Toast.makeText(context,"TimeChangedReceiver  $timeInMilli " , Toast.LENGTH_SHORT).show()
        }
    }
}
