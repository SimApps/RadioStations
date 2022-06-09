package com.amirami.simapp.radiostations

import android.app.*
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.media3.session.MediaStyleNotificationHelper
import coil.imageLoader
import coil.request.ImageRequest
import com.amirami.simapp.radiostations.Exoplayer.NOTIFICATION_DISMISSED
import com.amirami.simapp.radiostations.Exoplayer.PLAYPAUSE
import com.amirami.simapp.radiostations.Exoplayer.REC
import com.amirami.simapp.radiostations.Exoplayer.STOP
import com.amirami.simapp.radiostations.Exoplayer.STOPALL
import com.amirami.simapp.radiostations.Exoplayer.getIsPlaying
import com.amirami.simapp.radiostations.Exoplayer.initializePlayer
import com.amirami.simapp.radiostations.Exoplayer.isOreoPlus
import com.amirami.simapp.radiostations.Exoplayer.is_downloading
import com.amirami.simapp.radiostations.Exoplayer.is_playing_recorded_file
import com.amirami.simapp.radiostations.Exoplayer.mMediaSession
import com.amirami.simapp.radiostations.Exoplayer.notifi_CHANNEL_ID
import com.amirami.simapp.radiostations.Exoplayer.ongoing
import com.amirami.simapp.radiostations.Exoplayer.pausePlayer
import com.amirami.simapp.radiostations.Exoplayer.playPauseIcon
import com.amirami.simapp.radiostations.Exoplayer.playWhenReady
import com.amirami.simapp.radiostations.Exoplayer.player
import com.amirami.simapp.radiostations.Exoplayer.releasePlayer
import com.amirami.simapp.radiostations.Exoplayer.showWhen
import com.amirami.simapp.radiostations.Exoplayer.startPlayer
import com.amirami.simapp.radiostations.MainActivity.Companion.GlobalImage
import com.amirami.simapp.radiostations.MainActivity.Companion.GlobalRadioName
import com.amirami.simapp.radiostations.MainActivity.Companion.icyandState
import com.amirami.simapp.radiostations.MainActivity.Companion.imagedefaulterrorurl
import com.amirami.simapp.radiostations.RadioFunction.icyandStateWhenPlayRecordFiles


class NotificationChannelService : Service() {
    val notifID=93696
    val requestCode=93695
    val immutableFlag = if (Build.VERSION.SDK_INT >= 23) FLAG_IMMUTABLE else 0
  /*  override fun onCreate() {
        super.onCreate()

        /* mMediaSession = MediaSessionCompat(this, "media session")
         mMediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
         mMediaSession!!.setCallback(object : MediaSessionCompat.Callback() {
             override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
                 handleMediaButton(mediaButtonEvent, applicationContext)
                 return super.onMediaButtonEvent(mediaButtonEvent)
             }
         })


         audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

         */
        //initMediaSession(this)




        // The service is being created
        //Toast.makeText(application, "onCreate", Toast.LENGTH_SHORT).show()
    }*/

    private fun handlePlayPause() {

        //playWhenReady = true
        if (playWhenReady && getIsPlaying) {
            pausePlayer( this)
        }
        else {
            if(player==null){
                if(is_playing_recorded_file) initializePlayer(this,true)
                else initializePlayer(this,false)
            }

            startPlayer()

        }

      //  if (player!=null) player!!.addListener(Exoplayer.playbackStateListener(applicationContext))

    }

    private fun   handleREC(){
        if(is_downloading) {
            MainActivity.downloader?.cancelDownload()
        }
      else if(!is_playing_recorded_file && !is_downloading)  {
            RadioFunction.getDownloader(applicationContext )
            MainActivity.downloader?.download()
        }

    }


    private fun  handleSTOP(){
        releasePlayer(applicationContext)
    }

    private fun  handleSTOPALL(){
        if(!is_downloading) {
            RadioFunction.stopService(this)
        }
        releasePlayer(applicationContext)


    }

    fun getContentIntent(): PendingIntent {
        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, MainActivity::class.java)
// Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0,immutableFlag or FLAG_UPDATE_CURRENT)
        }


        return resultPendingIntent!!

    }
    fun getIntent(action: String): PendingIntent {
        val intent = Intent(this, ControlActionsListener::class.java)
        intent.action = action

        return  getBroadcast(applicationContext, requestCode, intent, immutableFlag or FLAG_UPDATE_CURRENT)



    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        lateinit var  notification: NotificationCompat.Builder


        when (intent?.action) {

            PLAYPAUSE -> handlePlayPause()
            REC -> handleREC()
            STOP -> handleSTOP()
            STOPALL -> handleSTOPALL()
        }
        //   playPauseIcon = if (getIsPlaying()) R.drawable.ic_pause else R.drawable.ic_play
        val notifWhen = 0L
        if (isOreoPlus()) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = resources.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            NotificationChannel(notifi_CHANNEL_ID, name, importance).apply {
                enableLights(false)
                enableVibration(false)
                notificationManager.createNotificationChannel(this)
            }
        }


        Exoplayer.Observer.subscribenotificztion("Main text view", icyandStateWhenPlayRecordFiles(icyandState, ""))





        // val radio_name = intent?.getStringExtra("input_radio_name")
        //   val radio_country = intent?.getStringExtra("input_radio_country")

        // The service is starting, due to a call to startService()

        val notificationDismissedIntent = Intent(this, NotificationDismissedReceiver::class.java).apply {
            action = NOTIFICATION_DISMISSED
        }



        val notificationDismissedPendingIntent =  getBroadcast(this, 0, notificationDismissedIntent, immutableFlag or FLAG_UPDATE_CURRENT)



      val request = ImageRequest.Builder(this@NotificationChannelService)
            .data( if (is_playing_recorded_file || GlobalImage=="") imagedefaulterrorurl else GlobalImage  )
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
                        .setOngoing(true)
                        .setContentTitle(GlobalRadioName)
                        .setContentText(icyandState)
                        //.setContentInfo("info")
                        // .setSubText("Sub Text")
                        .setColor(Color.BLUE)
                        .setWhen(notifWhen)
                        .setShowWhen(showWhen) // to verifie because in simple music player it go from false to true when state palyer is playin
                        .setOngoing(ongoing)
                        .setSmallIcon(R.drawable.radio)
                        .setContentIntent(/*pendingIntent*/getContentIntent())
                        // .setAutoCancel(true) // remove when clicked
                        .setOnlyAlertOnce(true)
                        .setLargeIcon(result.toBitmap())
                        .addAction(R.drawable.cancel, "CLOSE", getIntent(STOPALL)/*notificationDismissedPendingIntent*/)
                        .addAction(playPauseIcon, getString(R.string.playpause), getIntent(PLAYPAUSE))
                        //  .addAction(if(is_downloading){R.drawable.rec_on}else{R.drawable.rec_2}, "recon & recoff", getIntent(REC) /*favactionIntent*/)
                        .addAction(if(is_downloading) R.drawable.rec_on else R.drawable.stop_2, "stop", getIntent(STOP))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        //.setContentIntent(playpauseactionIntent)
                        //.setDeleteIntent(pendingIntent)
                        .setDeleteIntent(notificationDismissedPendingIntent)
                        .setChannelId(notifi_CHANNEL_ID)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                        .setStyle(
                           MediaStyleNotificationHelper.MediaStyle(mMediaSession)
                                .setShowActionsInCompactView(0, 1, 2)
                            // .setShowActionsInCompactView(1,2)
                        )



                 /*   mMediaSession!!.setMetadata(
                        MediaMetadataCompat.Builder()
                            .putBitmap(
                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                                result.toBitmap()
                            )//R.drawable.radio
                            .putString(
                                MediaMetadata.METADATA_KEY_TITLE, /*radio_name*/
                                GlobalRadioName
                            )
                            .putString(MediaMetadata.METADATA_KEY_ARTIST, icyandState)
                            .build()
                    )*/



                    startForeground(notifID, notification.build())

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
                        .setOngoing(true)
                        .setContentTitle(GlobalRadioName)
                        .setContentText(icyandState)
                        //.setContentInfo("info")
                        // .setSubText("Sub Text")
                        .setColor(Color.BLUE)
                        .setWhen(notifWhen)
                        .setShowWhen(showWhen) // to verifie because in simple music player it go from false to true when state palyer is playin
                        .setOngoing(ongoing)
                        .setSmallIcon(R.drawable.radio)
                        .setContentIntent(/*pendingIntent*/getContentIntent())
                        // .setAutoCancel(true) // remove when clicked
                        .setOnlyAlertOnce(true)
                        .setLargeIcon( BitmapFactory.decodeResource(this.resources,
                            R.drawable.radioerror) )
                        .addAction(R.drawable.cancel, "CLOSE", getIntent(STOPALL)/*notificationDismissedPendingIntent*/)
                        .addAction(playPauseIcon, getString(R.string.playpause), getIntent(PLAYPAUSE))
                        //  .addAction(if(is_downloading){R.drawable.rec_on}else{R.drawable.rec_2}, "recon & recoff", getIntent(REC) /*favactionIntent*/)
                        .addAction(if(is_downloading) R.drawable.rec_on else R.drawable.stop_2, "stop", getIntent(STOP))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        //.setContentIntent(playpauseactionIntent)
                        //.setDeleteIntent(pendingIntent)
                        .setDeleteIntent(notificationDismissedPendingIntent)
                        .setChannelId(notifi_CHANNEL_ID)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setStyle(MediaStyleNotificationHelper.MediaStyle(mMediaSession)
                            .setShowActionsInCompactView(0, 1, 2)
                           // .setShowActionsInCompactView(1,2)
                        )


                    startForeground(notifID, notification.build())

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

        return START_STICKY  //START_NOT_STICKY
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


    override fun onDestroy() {
        super.onDestroy()
        // The service is no longer used and is being destroyed
        if (player == null){
            stopSelf()
            stopForeground(true)

        }
       // mMediaSession?.isActive = false
        //Toast.makeText(application, "onDestroy", Toast.LENGTH_SHORT).show()


    }

    /*
        override fun onLowMemory() {
            Toast.makeText(application, "onDestroy", Toast.LENGTH_SHORT).show()
            super.onLowMemory()
        }

    */


}
