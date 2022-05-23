package com.amirami.simapp.radiostations.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.app.NotificationCompat
import com.amirami.simapp.radiostations.*
import com.amirami.simapp.radiostations.Exoplayer.notificationManager
import com.amirami.simapp.radiostations.hiltcontainer.RadioApplication

class AlarmReceiver : BroadcastReceiver() {
    private val backupnotifname = "backup-01"
    private var BACKUP_NOTIFICATION_ID = 2
    override fun onReceive(context: Context?, intent: Intent?) {
        // Generate an Id for each notification
        val id = 101010//System.currentTimeMillis() / 1000
        // Get the Notification manager service
        val am = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = "Alarm"
            val importance = NotificationManager.IMPORTANCE_HIGH
            NotificationChannel("AlarmId", name, importance).apply {
                enableLights(true)
                enableVibration(true)
                notificationManager.createNotificationChannel(this)
            }
        }

        fun handleSTOP() {
            Exoplayer.releaseAlarmPlayer()
            am.cancel(id)
        }

        when (intent?.action) {
            Exoplayer.STOP -> handleSTOP()
        }

        fun getIntent(action: String): PendingIntent {
            intent?.action = action
            return PendingIntent.getBroadcast(context, 0, intent!!, 0)
        }

        diableBootReceiver(context)
        if (hasInternetConnection(context)) {
            MainActivity.GlobalRadiourl = androidx.preference.PreferenceManager
                .getDefaultSharedPreferences(context).getString("radioURL", "http://icecast4.play.cz/crojazz256.mp3")!!
            Exoplayer.initializeAlarmPlayer(context)

            Exoplayer.startPlayer()
        }
        else PlaySystemAlarm(context)

        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString("radioURL", "Empty").apply()

        // Create the notification to be shown
        val mBuilder = NotificationCompat.Builder(context, "AlarmId")
            .setSmallIcon(R.drawable.ic_add_alarm)
            .setContentTitle("Alarm")
            .setTicker("setTicker")
            .setContentText("message")
            .setContentInfo("setContentInfo")
            .setContentText("Click to Stop ")
            .setPriority(NotificationCompat.PRIORITY_MAX)// this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setColor(Color.BLUE)
            .setContentIntent(getIntent(Exoplayer.STOP))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            //  .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
            .setStyle(NotificationCompat.BigTextStyle())



        // Show a notification
        am.notify(id.toInt(), mBuilder.build())
    }


    private fun PlaySystemAlarm(context: Context) {

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.alarm_backup)
            val description = context.getString(R.string.alarm_back_desc)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(backupnotifname, name, importance)
            channel.description = description
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            channel.setSound(soundUri, audioAttributes)

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, backupnotifname)
                .setSmallIcon(R.drawable.ic_add_alarm)
                .setContentTitle(context.getString(R.string.action_alarm))
                .setContentText(context.getString(R.string.alarm_fallback_info))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
        notificationManager.notify(BACKUP_NOTIFICATION_ID, mBuilder.build())
    }


    private fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    private fun diableBootReceiver(context: Context) {
        val receiver = ComponentName(context, BootCompleteReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}