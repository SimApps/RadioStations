package com.amirami.simapp.radiostations.alarm

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi

@UnstableApi object Utils {
    const val requestalarmId = 11421
     val immutableFlag = if (Build.VERSION.SDK_INT >= 23) /*PendingIntent.FLAG_IMMUTABLE*/ PendingIntent.FLAG_MUTABLE else 0

    fun setAlarm(context: Context, timeOfAlarm: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action =  Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    context.startActivity(intent)
                }
            }
            else setAlarms(context, timeOfAlarm)

        }
        else setAlarms(context, timeOfAlarm)



    }
    fun setAlarms(context: Context, timeOfAlarm: Long) {
        // Intent to start the Broadcast Receiver
        val broadcastIntent = Intent(context, AlarmReceiver::class.java)

        // Utils.setAlarm(context, timeOfAlarm)
        val pIntent = getBroadcast(
            context,
            requestalarmId,
            broadcastIntent,
            immutableFlag or FLAG_UPDATE_CURRENT
        )

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (System.currentTimeMillis() < timeOfAlarm) {

            val alarmClockInfo = AlarmClockInfo(timeOfAlarm, pIntent)
            alarmMgr.setAlarmClock(alarmClockInfo, pIntent)

            enableBootReceiver(context)
        }
        else {
            val alarmClockInfo = AlarmClockInfo(timeOfAlarm + 86400000L/* add one day*/, pIntent)
            alarmMgr.setAlarmClock(alarmClockInfo, pIntent)

            enableBootReceiver(context)
        }
    }

    fun cancelAlarm(context: Context) {
        // Intent to start the Broadcast Receiver
        val broadcastIntent = Intent(context, AlarmReceiver::class.java)

        val pIntent = getBroadcast(
            context,
            requestalarmId,
            broadcastIntent,
            immutableFlag or FLAG_UPDATE_CURRENT
        )
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (pIntent != null) {


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
