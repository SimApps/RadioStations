package com.amirami.simapp.radiostations.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amirami.simapp.radiostations.data.datastore.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootCompleteReceiver @Inject constructor(

    var dataViewModel: DataViewModel
): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED) ||
            intent?.action == "android.intent.action.BOOT_COMPLETED" ||
            intent?.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {

            if (dataViewModel.getTimeInMillis() != 0) {
                context?.let {
                    Utils.setAlarm(
                        it,
                        dataViewModel.getTimeInMillis().toLong()
                    )



                    if (System.currentTimeMillis() > dataViewModel.getTimeInMillis().toLong()) {
                        dataViewModel.saveTimeInMillis((dataViewModel.getTimeInMillis().toLong() + 86400000L).toInt())


                    }


                }
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
