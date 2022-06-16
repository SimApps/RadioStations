package com.amirami.simapp.radiostations.alarm

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

object Utils {
    const val requestalarmId = 11421
    private val immutableFlag = if (Build.VERSION.SDK_INT >= 23) FLAG_IMMUTABLE else 0


    fun setAlarm(context: Context, timeOfAlarm: Long) {

        // Intent to start the Broadcast Receiver
        val broadcastIntent = Intent(context, AlarmReceiver::class.java)

        // Utils.setAlarm(context, timeOfAlarm)
        val pIntent = getBroadcast(
            context, requestalarmId,
            broadcastIntent,
            immutableFlag or FLAG_UPDATE_CURRENT
        )

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (System.currentTimeMillis() < timeOfAlarm) {
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong("timeInMilli", timeOfAlarm).apply()

            val alarmClockInfo = AlarmClockInfo(timeOfAlarm, pIntent)
            alarmMgr.setAlarmClock(alarmClockInfo, pIntent)

            enableBootReceiver(context)

        } else {
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong("timeInMilli", timeOfAlarm + 86400000L).apply()
            val alarmClockInfo = AlarmClockInfo(timeOfAlarm + 86400000L/* add one day*/, pIntent)
            alarmMgr.setAlarmClock(alarmClockInfo, pIntent)

            enableBootReceiver(context)

        }

    }



    fun cancelAlarm(context: Context) {

        // Intent to start the Broadcast Receiver
        val broadcastIntent = Intent(context, AlarmReceiver::class.java)

        val pIntent = getBroadcast(
            context, requestalarmId,
            broadcastIntent,
            immutableFlag or FLAG_UPDATE_CURRENT
        )
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



    fun enableBootReceiver(context: Context) {
        val receiver = ComponentName(context, BootCompleteReceiver::class.java)

        context.packageManager?.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun diableBootReceiver(context: Context) {
        val receiver = ComponentName(context, BootCompleteReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}