package com.amirami.simapp.radiostations.alarm

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import com.amirami.simapp.radiostations.Exoplayer
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.MainActivity.Companion.fromAlarm
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.alarm.Utils.diableBootReceiver
import com.amirami.simapp.radiostations.alarm.Utils.immutableFlag
import com.amirami.simapp.radiostations.data.datastore.viewmodel.DataViewModel
import com.amirami.simapp.radiostations.utils.Constatnts.ALARM_CHANNEL_ID
import com.amirami.simapp.radiostations.utils.Constatnts.ALARM_ID
import com.amirami.simapp.radiostations.utils.Constatnts.ALARM_NOTIF_NAME
import com.amirami.simapp.radiostations.utils.Constatnts.EXTRA_MESSAGE
import com.amirami.simapp.radiostations.utils.Constatnts.EXTRA_MESSAGE_VALUE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@UnstableApi class AlarmReceiver @Inject constructor(

    var dataViewModel: DataViewModel
) : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        // Generate an Id for each notification

        // Get the Notification manager service
        val am = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Exoplayer.isOreoPlus()) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            NotificationChannel(ALARM_CHANNEL_ID, ALARM_NOTIF_NAME, importance).apply {
                // Configure the notification channel.
                description = "Channel description"
                enableLights(true)
                lightColor = Color.RED
                //  enableVibration(true)
                // vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
                notificationManager.createNotificationChannel(this)
            }
        }

        fun handleSTOP() {
            Exoplayer.releasePlayer(context)
            am.cancel(ALARM_ID)
        }

        when (intent?.action) {
            Exoplayer.STOP -> handleSTOP()
        }

        fun getIntent(action: String): PendingIntent {
            // fromAlarm=false
            intent?.action = action
            return PendingIntent.getBroadcast(
                context,
                ALARM_ID,
                intent!!,
                immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        diableBootReceiver(context)
        fromAlarm = true

        /*     if (hasInternetConnection(context)) {
                 MainActivity.GlobalRadiourl = androidx.preference.PreferenceManager
                     .getDefaultSharedPreferences(context).getString("radioURL", "http://icecast4.play.cz/crojazz256.mp3")!!
                 Exoplayer.initializePlayer(context,false)

                 Exoplayer.startPlayer()

             }
             else PlaySystemAlarm(context)
     */


        dataViewModel.saveRadioUrl("Empty")

        fun getfullScreenPendingIntent(): PendingIntent {
            val fullScreenIntent = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(
                context,
                0,
                fullScreenIntent,
                immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        // Create the notification to be shown
        val mBuilder = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_add_alarm)
            .setContentTitle("Alarm")
            .setTicker("setTicker")
            .setContentText("message")
            .setContentInfo("setContentInfo")
            .setContentText("Click to Stop ")
            .setPriority(NotificationCompat.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
            .setFullScreenIntent(getfullScreenPendingIntent(), true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setColor(Color.BLUE)
            // .setOngoing(true)
            .setLights(0xFFFFFF, 1000, 1000)
            .setContentIntent(getIntent(Exoplayer.STOP))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            //   .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            //  .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
            .setStyle(NotificationCompat.BigTextStyle())

        // Show a notification
        am.notify(ALARM_ID, mBuilder.build())

        val intent1 = Intent()
        intent1.setClassName(context.packageName, MainActivity::class.java.name).apply {
            putExtra(EXTRA_MESSAGE, EXTRA_MESSAGE_VALUE)
        }
        intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent1)
    }
}
