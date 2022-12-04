package com.amirami.simapp.radiostations

import android.app.*
import android.app.PendingIntent.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.session.MediaStyleNotificationHelper
import coil.imageLoader
import coil.request.ImageRequest
import com.amirami.simapp.radiostations.Exoplayer.NOTIFICATION_DISMISSED
import com.amirami.simapp.radiostations.Exoplayer.PLAYPAUSE
import com.amirami.simapp.radiostations.Exoplayer.STOP
import com.amirami.simapp.radiostations.Exoplayer.STOPALL
import com.amirami.simapp.radiostations.Exoplayer.isOreoPlus
import com.amirami.simapp.radiostations.Exoplayer.is_downloading
import com.amirami.simapp.radiostations.Exoplayer.is_playing_recorded_file
import com.amirami.simapp.radiostations.Exoplayer.mMediaSession
import com.amirami.simapp.radiostations.Exoplayer.notifi_CHANNEL_ID
import com.amirami.simapp.radiostations.Exoplayer.playPauseIcon
import com.amirami.simapp.radiostations.Exoplayer.player
import com.amirami.simapp.radiostations.MainActivity.Companion.GlobalRadioName
import com.amirami.simapp.radiostations.MainActivity.Companion.icyandState
import com.amirami.simapp.radiostations.MainActivity.Companion.imageLinkForNotification
import com.amirami.simapp.radiostations.MainActivity.Companion.imagedefaulterrorurl
import com.amirami.simapp.radiostations.RadioFunction.icyandStateWhenPlayRecordFiles

class NotificationChannelService : Service() {
    val notifID = 93696
    val immutableFlag = if (Build.VERSION.SDK_INT >= 23) FLAG_IMMUTABLE else 0

    /*  private fun handlePlayPause() {
         if (playWhenReady && getIsPlaying) pausePlayer()
          else {
              if(player==null){
                  if(is_playing_recorded_file) initializePlayer(this,true)
                  else initializePlayer(this,false)
              }
              startPlayer()
          }
      }

  */

    /* private fun  handleSTOP(){
        // releasePlayer(this)
     }

     private fun  handleSTOPALL(){
              errorToast(this,"eeeeeee")
            // releasePlayer(this)
     }*/
    private fun getIntent(action: String): PendingIntent {
        val intent = Intent(this, ControlActionsListener::class.java)
        intent.action = action
        return getBroadcast(applicationContext, notifID, intent, immutableFlag or FLAG_UPDATE_CURRENT)
    }

    private fun getContentIntent(): PendingIntent {
        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, MainActivity::class.java)
// Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(notifID, immutableFlag or FLAG_UPDATE_CURRENT)
        }
        return resultPendingIntent!!
    }

    lateinit var notification: Notification

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY_COMPATIBILITY
        else {
            if (isOreoPlus()) {
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val name = resources.getString(R.string.app_name)
                val importance = NotificationManager.IMPORTANCE_LOW
                NotificationChannel(notifi_CHANNEL_ID, name, importance).apply {
                    enableLights(false)
                    enableVibration(false)
                    notificationManager.createNotificationChannel(this)
                }
            }

            /*   when (intent.action) {
                   PLAYPAUSE -> handlePlayPause()
                   STOP -> handleSTOP()
                   STOPALL -> handleSTOPALL()
               }*/
            /*   if (isOreoPlus()) {
                setupFakeNotification()
            }*/

            Exoplayer.Observer.subscribenotificztion("Main text view", icyandStateWhenPlayRecordFiles(icyandState, ""))

            // The service is starting, due to a call to startService()

            val notificationDismissedIntent = Intent(this, NotificationDismissedReceiver::class.java).apply {
                action = NOTIFICATION_DISMISSED
            }
            //   intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val notificationDismissedPendingIntent = getBroadcast(this, notifID, notificationDismissedIntent, immutableFlag or FLAG_UPDATE_CURRENT)

            val request = ImageRequest.Builder(this@NotificationChannelService)
                .data(
                    if (is_playing_recorded_file || imageLinkForNotification == "") imagedefaulterrorurl
                    else imageLinkForNotification
                )
                .allowConversionToBitmap(true)
                .target(
                    onStart = { placeholder ->
                        // Handle the placeholder drawable.
                    },
                    onSuccess = { result ->
                        // Handle the successful result.
                        notification = NotificationCompat.Builder(
                            this@NotificationChannelService,
                            notifi_CHANNEL_ID
                        )
                            .setContentTitle(GlobalRadioName)
                            .setContentText(icyandState)
                            // .setContentInfo("info")
                            // .setSubText("Sub Text")
                            .setColor(Color.BLUE)
                            // .setWhen(0L)
                            // .setShowWhen(showWhen) // to verifie because in simple music player it go from false to true when state palyer is playin
                            //  .setOngoing(ongoing)
                            .setSmallIcon(R.drawable.radio)
                            .setContentIntent(/*pendingIntent*/getContentIntent())
                            // .setAutoCancel(true) // remove when clicked
                            .setOnlyAlertOnce(true)
                            .setLargeIcon(result.toBitmap())
                            .addAction(R.drawable.cancel, "CLOSE", getIntent(STOPALL)/*notificationDismissedPendingIntent*/)
                            .addAction(playPauseIcon, getString(R.string.playpause), getIntent(PLAYPAUSE))
                            //  .addAction(if(is_downloading){R.drawable.rec_on}else{R.drawable.rec_2}, "recon & recoff", getIntent(REC) /*favactionIntent*/)
                            .addAction(if (is_downloading) R.drawable.rec_on else R.drawable.stop_2, "stop", getIntent(STOP))
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            // .setContentIntent(playpauseactionIntent)
                            // .setDeleteIntent(pendingIntent)
                            .setDeleteIntent(notificationDismissedPendingIntent)
                            //   .setChannelId(notifi_CHANNEL_ID)
                            .setCategory(NotificationCompat.CATEGORY_SERVICE)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setStyle(
                                MediaStyleNotificationHelper.MediaStyle(mMediaSession)
                                    .setShowActionsInCompactView(0, 1, 2)
                                // .setShowActionsInCompactView(1,2)
                            )
                            .build()
                        startForeground(notifID, notification)
// delay foreground state updating a bit, so the notification can be swiped away properly after initial display
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (player != null) {
                                if (!player!!.playWhenReady) {
                                    stopForeground(false)
                                }
                            }
                        }, 200L)
                    },
                    onError = { error ->
                        // Handle the error drawable.
                        //  imageView.load(url)

                        notification = NotificationCompat.Builder(
                            this@NotificationChannelService,
                            notifi_CHANNEL_ID
                        )
                            .setContentTitle(GlobalRadioName)
                            .setContentText(icyandState)
                            // .setContentInfo("info")
                            // .setSubText("Sub Text")
                            .setColor(Color.BLUE)
                            // .setWhen(notifWhen)
                            // .setShowWhen(showWhen) // to verifie because in simple music player it go from false to true when state palyer is playin
                            // .setOngoing(ongoing)
                            .setSmallIcon(R.drawable.radio)
                            .setContentIntent(/*pendingIntent*/getContentIntent())
                            // .setAutoCancel(true) // remove when clicked
                            .setOnlyAlertOnce(true)
                            .setLargeIcon(
                                BitmapFactory.decodeResource(
                                    this.resources,
                                    R.drawable.radioerror
                                )
                            )
                            .addAction(R.drawable.cancel, "CLOSE", getIntent(STOPALL)/*notificationDismissedPendingIntent*/)
                            .addAction(playPauseIcon, getString(R.string.playpause), getIntent(PLAYPAUSE))
                            //  .addAction(if(is_downloading){R.drawable.rec_on}else{R.drawable.rec_2}, "recon & recoff", getIntent(REC) /*favactionIntent*/)
                            .addAction(if (is_downloading) R.drawable.rec_on else R.drawable.stop_2, "stop", getIntent(STOP))
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            // .setContentIntent(playpauseactionIntent)
                            // .setDeleteIntent(pendingIntent)
                            .setDeleteIntent(notificationDismissedPendingIntent)
                            //   .setChannelId(notifi_CHANNEL_ID)
                            .setCategory(NotificationCompat.CATEGORY_SERVICE)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setStyle(
                                MediaStyleNotificationHelper.MediaStyle(mMediaSession)
                                    .setShowActionsInCompactView(0, 1, 2)
                                // .setShowActionsInCompactView(1,2)
                            ).build()

                        startForeground(notifID, notification)

// delay foreground state updating a bit, so the notification can be swiped away properly after initial display
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (player != null) {
                                if (!player!!.playWhenReady) {
                                    stopForeground(false)
                                }
                            }
                        }, 200L)
                    }

                )
                .build()
            this@NotificationChannelService.imageLoader.enqueue(request)

            //    return START_STICKY  //START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // A client is binding to the service with bindService()
        return null
    }

    /*  override fun onUnbind(intent:Intent):Boolean {
          // All clients have unbound with unbindService()
         // ExoPlayer.mMediaSession!!.release()

          return super.onUnbind(intent)
      }
  */
    override fun onRebind(intent: Intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    /* override fun onTaskRemoved(rootIntent: Intent?) {
         super.onTaskRemoved(rootIntent)
         stopSelf()
         stopForeground(true)
     }*/

    override fun onDestroy() {
        super.onDestroy()
        // The service is no longer used and is being destroyed
        Exoplayer.releasePlayer(this)
        stopSelf()
        stopForeground(true)
    }

    /*
        override fun onLowMemory() {
            Toast.makeText(application, "onDestroy", Toast.LENGTH_SHORT).show()
            super.onLowMemory()
        }

    */

/*
    private fun setupFakeNotification() {
      val  notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = resources.getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_LOW
        NotificationChannel(notifi_CHANNEL_ID, name, importance).apply {
            enableLights(false)
            enableVibration(false)
            notificationManager.createNotificationChannel(this)
        }
        val  notification = NotificationCompat.Builder(applicationContext, notifi_CHANNEL_ID)
            .setContentTitle("")
            .setContentText("")
            .setSmallIcon(R.drawable.radioerror)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MIN) // max
            .setChannelId(notifi_CHANNEL_ID)
            .setOnlyAlertOnce(true)
            .setCategory(Notification.CATEGORY_SERVICE)

        startForeground(1, notification.build())

        // notificationManager.notify( 1, notification.build() )
    }
*/
}
