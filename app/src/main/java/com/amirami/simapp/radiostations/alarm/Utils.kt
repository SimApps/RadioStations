package com.amirami.simapp.radiostations.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

object Utils {
const val requestalarmId= 11421
    fun setAlarm(context: Context, timeOfAlarm: Long) {

        // Intent to start the Broadcast Receiver
        val broadcastIntent = Intent(context, AlarmReceiver::class.java)

       // Utils.setAlarm(context, timeOfAlarm)
       val  pIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           PendingIntent.getBroadcast(context, requestalarmId, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE/*PendingIntent.FLAG_UPDATE_CURRENT*/)
       } else {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
               PendingIntent.getBroadcast(context, requestalarmId, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE)
           } else {
               PendingIntent.getBroadcast(context, requestalarmId, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT )

           }
       }
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
/*
        // Setting up AlarmManager

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, (timeOfAlarm.toInt() / (1000 * 60 * 60) % 24))
            set(Calendar.MINUTE, (timeOfAlarm.toInt() / (1000 * 60) % 60))
        }

                 //   val seconds = (milliseconds / 1000) as Int % 60
         //   val minutes = (milliseconds / (1000 * 60) % 60)
         //   val hours = (milliseconds / (1000 * 60 * 60) % 24)
        */

        if (System.currentTimeMillis() < timeOfAlarm) {
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong("timeInMilli", timeOfAlarm).apply()

            val alarmClockInfo = AlarmClockInfo(timeOfAlarm, pIntent)
            alarmMgr.setAlarmClock(alarmClockInfo, pIntent)

            val receiver = ComponentName(context, BootCompleteReceiver::class.java)

            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            /*  alarmMgr.setAlarmClock(
                  AlarmManager.RTC,
                  timeOfAlarm,
                  pIntent
              */
         /*   alarmMgr.setInexactRepeating(
                    AlarmManager.RTC,
                    timeOfAlarm,AlarmManager.INTERVAL_HALF_DAY,
                    pIntent
            )*/
         /*   alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                timeOfAlarm,AlarmManager.INTERVAL_DAY,
                    pIntent
            )*/
        }
        else {
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong("timeInMilli", timeOfAlarm +86400000L).apply()
            val alarmClockInfo = AlarmClockInfo(timeOfAlarm +86400000L/* add one day*/, pIntent)
            alarmMgr.setAlarmClock(alarmClockInfo, pIntent)

            val receiver = ComponentName(context, BootCompleteReceiver::class.java)

            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

        }

    }


    fun cancelAlarm(context: Context){

        // Intent to start the Broadcast Receiver
        val broadcastIntent = Intent(context, AlarmReceiver::class.java)

        // Utils.setAlarm(context, timeOfAlarm)
        val  pIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(context, requestalarmId, broadcastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
            }
else  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(context, requestalarmId, broadcastIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or FLAG_MUTABLE)
            } else {
                PendingIntent.getBroadcast(context, requestalarmId, broadcastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)            }
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (pIntent != null) {
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("radioURL", "Empty").apply()

            val receiver = ComponentName(context, BootCompleteReceiver::class.java)
            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )

            alarmMgr.cancel(pIntent)


        }



    }
}