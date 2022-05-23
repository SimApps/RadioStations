package com.amirami.simapp.radiostations

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaMetadata
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.media.session.MediaButtonReceiver
import coil.imageLoader
import coil.request.ImageRequest
import com.amirami.simapp.radiostations.Exoplayer.NOTIFICATION_DISMISSED
import com.amirami.simapp.radiostations.Exoplayer.PLAYPAUSE
import com.amirami.simapp.radiostations.Exoplayer.REC
import com.amirami.simapp.radiostations.Exoplayer.STOP
import com.amirami.simapp.radiostations.Exoplayer.STOPALL
import com.amirami.simapp.radiostations.Exoplayer.audioManager
import com.amirami.simapp.radiostations.Exoplayer.getIsPlaying
import com.amirami.simapp.radiostations.Exoplayer.initMediaSession
import com.amirami.simapp.radiostations.Exoplayer.initializePlayer
import com.amirami.simapp.radiostations.Exoplayer.isOreoPlus
import com.amirami.simapp.radiostations.Exoplayer.is_downloading
import com.amirami.simapp.radiostations.Exoplayer.is_playing_recorded_file
import com.amirami.simapp.radiostations.Exoplayer.mMediaSession
import com.amirami.simapp.radiostations.Exoplayer.notifi_CHANNEL_ID
import com.amirami.simapp.radiostations.Exoplayer.notificationManager
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


class NotificationChannelService : Service() {
    override fun onCreate() {
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
        initMediaSession(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (isOreoPlus()) OreoAudioFocusHandler(applicationContext)



        // The service is being created
        //Toast.makeText(application, "onCreate", Toast.LENGTH_SHORT).show()
    }

    private fun handlePlayPause() {

        playWhenReady = true
        if (getIsPlaying()) {
            pausePlayer( this)
        } else {
            if(player==null){
                if(is_playing_recorded_file){
                    Exoplayer.initializePlayerRecodedradio(this)
                }else{
                    initializePlayer(this)
                    // video_view.player = player

                }
            }

            startPlayer()

        }

        if (player!=null){
            player!!.addListener(Exoplayer.Playerlistener)
        }
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
        releasePlayer()
    }

    private fun  handleSTOPALL(){
        if(!is_downloading) {
            RadioFunction.stopService(this)
        }
        releasePlayer()


    }

    fun getContentIntent(): PendingIntent {
        val contentIntent = Intent(Intent.ACTION_VIEW, "radiobroadcasting://simappr.com/favoritfrag".toUri(),
            this, MainActivity::class.java)
       // val contentIntent = Intent(this, MainActivity::class.java)
        return getActivity(this, 0, contentIntent, 0)/* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getBroadcast(
                applicationContext,
                0,
                contentIntent,
                FLAG_IMMUTABLE or FLAG_CANCEL_CURRENT
            )
        } else {
            getBroadcast(
                applicationContext,
                0,
                contentIntent,
                FLAG_CANCEL_CURRENT // ignore it aleready fixed
            )
        }*/


    }
    fun getIntent(action: String): PendingIntent {
        val intent = Intent(this, ControlActionsListener::class.java)
        intent.action = action
        return  getBroadcast(applicationContext, 0, intent, 0)/* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getBroadcast(
                applicationContext,
                0,
                intent,
                FLAG_IMMUTABLE or FLAG_CANCEL_CURRENT
            )
        } else {
            getBroadcast(
                applicationContext,
                0,
                intent,
                FLAG_CANCEL_CURRENT // ignore it aleready fixed
            )
        }*/


    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

       /* val   radiDBList= dbHandler.getCustomers(/*this*/)

        if (radiDBList.size>0){

            loop@ for (i in 0 until radiDBList.size){
                val radio: RadioVariables =radiDBList[i]
                if (GlobalRadioId ==radio.stationuuid) {
                    likedislike=R.drawable.ic_heart

                    break@loop
                }else{
                    likedislike=R.drawable.ic_liked
                }
            }
        }
*/
        lateinit var  notification: NotificationCompat.Builder
        val actions = intent?.action

        if (isOreoPlus() && actions != REC && actions != STOP && actions != PLAYPAUSE && actions != PLAYPAUSE) {
            if (isOreoPlus() && actions != STOP && actions != REC && actions != PLAYPAUSE) {
                setupFakeNotification()
            }
        }
        when (actions) {

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

        if (!mMediaSession!!.isActive) MediaButtonReceiver.handleIntent(mMediaSession!!, intent)


        Exoplayer.Observer.changeText("Main text view", icyandState)
        Exoplayer.Observer.changeText("text view", icyandState)
        Exoplayer.Observer.subscribenotificztion("Main text view", icyandState)





        // val radio_name = intent?.getStringExtra("input_radio_name")
        //   val radio_country = intent?.getStringExtra("input_radio_country")

        // The service is starting, due to a call to startService()

        val notificationDismissedIntent = Intent(this, NotificationDismissedReceiver::class.java).apply {
            action = NOTIFICATION_DISMISSED
        }


        val notificationDismissedPendingIntent =  getBroadcast(this, 0, notificationDismissedIntent, FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getBroadcast(
                    this,
                    0,
                    notificationDismissedIntent,
                    FLAG_IMMUTABLE or FLAG_CANCEL_CURRENT
                )
            } else {
                getBroadcast(
                    this,
                    0,
                    notificationDismissedIntent,
                     FLAG_CANCEL_CURRENT // ignore it aleready fixed
                )
            }*/


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
                        .setShowWhen(false)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        //.setContentIntent(playpauseactionIntent)
                        //.setDeleteIntent(pendingIntent)
                        .setDeleteIntent(notificationDismissedPendingIntent)
                        .setChannelId(notifi_CHANNEL_ID)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setStyle(
                            androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2)
                                .setMediaSession(mMediaSession?.sessionToken)
                        )

                    mMediaSession!!.setMetadata(
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
                    )
                    //.setWhen()
                    //.setUsesChronometer(true)
                    // .build()


                    startForeground(1, notification.build())

// delay foreground state updating a bit, so the notification can be swiped away properly after initial display
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (player != null) {
                            if (!player!!.playWhenReady) {
                                stopForeground(false)
                            }
                        }

                    }, 200L)

                    // favactionIntent=getIntent(PLAYPAUSE)
                    //   notification.build().flags = Notification.FLAG_ONGOING_EVENT
                    //   notificationManager.notify(1, notification.build())

                    /*     var playbackState = PlaybackStateCompat.STATE_PLAYING
                     if (player!=null){
                         if (player?.playWhenReady!!) { playbackState=PlaybackStateCompat.STATE_PLAYING } else{ playbackState=PlaybackStateCompat.STATE_PAUSED }

                     }else{
                         playbackState=PlaybackStateCompat.STATE_STOPPED
                     }


                        try {
                            mMediaSession!!.setPlaybackState(
                                PlaybackStateCompat.Builder()
                                    .setState(playbackState,
                                        PlaybackState.PLAYBACK_POSITION_UNKNOWN, 1.0f)
                                    .build())
                        } catch (ignored: IllegalStateException) {
                        }
*/
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
                        .setShowWhen(false)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        //.setContentIntent(playpauseactionIntent)
                        //.setDeleteIntent(pendingIntent)
                        .setDeleteIntent(notificationDismissedPendingIntent)
                        .setChannelId(notifi_CHANNEL_ID)
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setStyle(
                            androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2)
                                .setMediaSession(mMediaSession?.sessionToken)
                        )

                    mMediaSession!!.setMetadata(
                        MediaMetadataCompat.Builder()
                            .putBitmap(
                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                                BitmapFactory.decodeResource(this.resources,
                                    R.drawable.radioerror)
                            )//R.drawable.radio
                            .putString(
                                MediaMetadata.METADATA_KEY_TITLE, /*radio_name*/
                                GlobalRadioName
                            )
                            .putString(MediaMetadata.METADATA_KEY_ARTIST, icyandState)
                            .build()
                    )
                    //.setWhen()
                    //.setUsesChronometer(true)
                    // .build()


                    startForeground(1, notification.build())

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




        //do heavy work on a background thread
        //    stopSelf()





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
        if (player == null/* && focus_state==AudioManager.AUDIOFOCUS_REQUEST_GRANTED*/){
            stopSelf()
            stopForeground(true)

        }
        mMediaSession?.isActive = false
        //Toast.makeText(application, "onDestroy", Toast.LENGTH_SHORT).show()


    }

    /*
        override fun onLowMemory() {
            Toast.makeText(application, "onDestroy", Toast.LENGTH_SHORT).show()
            super.onLowMemory()
        }

    */
    @SuppressLint("NewApi")
    private fun setupFakeNotification() {
        //  val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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


}
