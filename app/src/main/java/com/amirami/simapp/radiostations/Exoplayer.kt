package com.amirami.simapp.radiostations

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.dash.DefaultDashChunkSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import com.amirami.simapp.radiostations.Exoplayer.Observer.Companion.changeImagePlayPause
import com.amirami.simapp.radiostations.MainActivity.Companion.GlobalRadiourl
import com.amirami.simapp.radiostations.MainActivity.Companion.GlobalstateString
import com.amirami.simapp.radiostations.MainActivity.Companion.downloader
import com.amirami.simapp.radiostations.MainActivity.Companion.fromAlarm
import com.amirami.simapp.radiostations.MainActivity.Companion.video_on
import com.amirami.simapp.radiostations.RadioFunction.icyandStateWhenPlayRecordFiles
import com.amirami.simapp.radiostations.utils.Constatnts.ALARM_ID
import com.amirami.simapp.radiostations.utils.Constatnts.ALARM_NOTIF_NAME

@UnstableApi object Exoplayer {
    const val notifi_CHANNEL_ID = "SimAPPcganelIDradioApp"

    const val packagename = "com.amirami.simapp.radiostations"
    private const val PATH = "$packagename.action."
    const val NOTIFICATION_DISMISSED = PATH + "NOTIFICATION_DISMISSED"
    const val PLAYPAUSE = PATH + "PLAYPAUSE"
    const val STOP = PATH + "STOP"
    const val STOPALL = PATH + "STOPALL"
    var totalTime: Long = 0
    var is_playing_recorded_file = false
    var is_downloading = false
    var player: ExoPlayer? = null
    lateinit var mMediaSession: MediaSession
    var getIsPlaying = false
    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    var playWhenReady = true
    var playPauseIcon = R.drawable.pause_2 // if (!getIsPlaying()) R.drawable.ic_pause else R.drawable.ic_play

    fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    fun initializePlayer(ctx: Context, isRecfile: Boolean, streamUrl: Uri) {
        if (streamUrl != Uri.parse("")) GlobalRadiourl = streamUrl
        is_playing_recorded_file = isRecfile

        val eContext = ctx.applicationContext

        if (is_downloading) {
            downloader?.cancelDownload() // to cancel download before playing new station
            is_downloading = false // when play and rec then press new station it wont work properly without this line
        }
        releasePlayer(ctx) // if removed player sttate get mess up

        val trackSelector = DefaultTrackSelector(eContext).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = ExoPlayer.Builder(eContext)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()

        if (!::mMediaSession.isInitialized) mMediaSession = MediaSession.Builder(eContext, player!!).build()

        player!!.seekTo(currentWindow, playbackPosition)

        val mediaSource = buildMediaSource(if (streamUrl != Uri.parse("")) streamUrl else GlobalRadiourl, ctx, isRecfile)

        player!!.setMediaSource(mediaSource, true)
        player!!.apply {
            playWhenReady = Exoplayer.playWhenReady
            addListener(playbackStateListener(ctx))
            repeatMode = Player.REPEAT_MODE_OFF
            prepare()
        }

        //  RadioFunction.startServices(ctx)// THIS LINE BECAUSE NOTIF DONT RESHOW WHEN DISMMISSED (only for rec files)
    }

    class Observer private constructor() {
        private val map: HashMap<String, TextView> = HashMap()
        private val mapnotify: HashMap<String, String> = HashMap()
        private val mapimage: HashMap<String, ImageView> = HashMap()

        companion object {
            private val instance = Observer()
            fun subscribe(viewKey: String, view: TextView) {
                val observer = instance
                observer.map[viewKey] = view
            }

            fun subscribeImagePlayPause(viewKey: String, image: ImageView) {
                val observer = instance
                observer.mapimage[viewKey] = image
            }

            fun subscribeImageRecord(viewKey: String, image: ImageView) {
                val observer = instance
                observer.mapimage[viewKey] = image
            }

            fun subscribenotificztion(viewKey: String, texet: String) {
                val observer = instance
                observer.mapnotify[viewKey] = texet
            }

            fun changesubscribenotificztion(viewKey: String, text: String) {
                val observer = instance
                if (observer.map.containsKey(viewKey)) {
                    val textView = observer.map[viewKey]
                    textView!!.text = text
                }
            }

            fun changeText(viewKey: String, text: String) {
                val observer = instance
                if (observer.map.containsKey(viewKey)) {
                    val textView = observer.map[viewKey]
                    textView!!.text = text
                }
                /*  else
                  {
                      // throw exception
                  }*/
            }

            fun changeImagePlayPause(viewKey: String, image: Int) {
                val observer = instance
                if (observer.mapimage.containsKey(viewKey)) {
                    val imageView = observer.mapimage[viewKey]
                    imageView!!.setImageResource(image)
                }
                /*  else
                  {
                      // throw exception
                  }*/
            }

            fun changeImageRecord(viewKey: String, image: Int) {
                val observer = instance
                if (observer.mapimage.containsKey(viewKey)) {
                    val imageView = observer.mapimage[viewKey]
                    imageView!!.setImageResource(image)
                }
                /*  else
                  {
                      // throw exception
                  }*/
            }
        }
    }

    fun releasePlayer(context: Context) {
        if (player != null) {
            if (is_downloading) downloader?.cancelDownload()
            else {
                playbackPosition = player!!.currentPosition
                currentWindow = player!!.currentMediaItemIndex
                player!!.playWhenReady = player!!.playWhenReady
                player!!.playbackState
                player!!.removeListener(playbackStateListener(context))
                mMediaSession.release()
                player?.stop()
                player!!.release()

                player = null
            }
        }
    }

    private fun buildMediaSource(uri: Uri, ctx: Context, isplayRecFil: Boolean): MediaSource {
        val type = Util.inferContentType(uri)
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            //  .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()

        player!!.addMediaItem(mediaItem)

        return if (isplayRecFil) {
            ProgressiveMediaSource
                .Factory(DefaultDataSource.Factory(ctx))
                .createMediaSource(mediaItem)
        } else when (type) {
      /*  C.TRACK_TYPE_VIDEO-> {

        }



        C.CONTENT_TYPE_SS -> {
            video_on = true
            val ssSourceFactory = DefaultSsChunkSource.Factory(
                //   icyHttpDataSourceFactory
                DefaultHttpDataSource.Factory()
            )
            val manifestDataSourceFactory =  DefaultHttpDataSource.Factory()
            SsMediaSource.Factory(ssSourceFactory, manifestDataSourceFactory).createMediaSource(
                mediaItem
            )
        }

       */
            C.CONTENT_TYPE_RTSP -> {
                video_on = true
                RtspMediaSource.Factory().createMediaSource(mediaItem)
            }
            C.CONTENT_TYPE_DASH -> {
                video_on = true

                val dashChunkSourceFactory = DefaultDashChunkSource.Factory(DefaultHttpDataSource.Factory())

                val manifestDataSourceFactory = DefaultHttpDataSource.Factory()

                DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
                    .createMediaSource(mediaItem)
            }

            C.CONTENT_TYPE_HLS -> {
                video_on = true
                HlsMediaSource.Factory(DefaultHttpDataSource.Factory())
                    .createMediaSource(mediaItem)
            }

            C.CONTENT_TYPE_OTHER -> {
                video_on = false
                ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                    .createMediaSource(mediaItem)
            }
            else -> {
                video_on = false
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }

    fun pausePlayer() {
        if (player != null) {
            player!!.playWhenReady = false
            player!!.playbackState
        }

        if (is_downloading) downloader?.cancelDownload()
    }

    fun startPlayer() {
        if (player != null) {
            player!!.playWhenReady = true
            player!!.playbackState
        }
    }

    private fun soundPlayer(raw_id: Float) {
        if (player != null) {
            player!!.volume = raw_id
        }
    }

    private fun playbackStateListener(ctx: Context) = object : Player.Listener {
        var icybackup = ""
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            getIsPlaying = isPlaying
            if (playWhenReady && isPlaying) {
                if (is_playing_recorded_file) totalTime = player?.duration!!
                else if (GlobalstateString == "UNKNOWN_STATE") {
                    Observer.changeText("Main text view", icyandStateWhenPlayRecordFiles(icybackup, ""))
                    Observer.changeText("text view", icyandStateWhenPlayRecordFiles(icybackup, ""))
                    Observer.changesubscribenotificztion("Main text view", icyandStateWhenPlayRecordFiles(icybackup, ""))
                }

                playPauseIcon = R.drawable.pause_2
                GlobalstateString = "Player.STATE_READY"
                changeImagePlayPause("Main image view", R.drawable.pause_2)
                changeImagePlayPause("image view", R.drawable.pause_2)
            } else if (playWhenReady && GlobalstateString != "Player.STATE_BUFFERING") {
                playPauseIcon = R.drawable.play_2
                GlobalstateString = "Player.STATE_PAUSED"
                changeImagePlayPause("Main image view", R.drawable.play_2)
                changeImagePlayPause("image view", R.drawable.play_2)
            }

            RadioFunction.startServices(ctx)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE // The player does not have any media to play.
                -> {
                    playPauseIcon = R.drawable.play_2
                    GlobalstateString = "Player.STATE_IDLE"
                    Observer.changesubscribenotificztion("Main text view", icyandStateWhenPlayRecordFiles("", ""))
                    Observer.changeText("Main text view", icyandStateWhenPlayRecordFiles("", ""))
                    Observer.changeText("text view", icyandStateWhenPlayRecordFiles("", ""))
                    changeImagePlayPause("Main image view", R.drawable.play_2)
                    changeImagePlayPause("image view", R.drawable.play_2)
                }
                Player.STATE_BUFFERING // The player needs to load media before playing.
                -> {
                    GlobalstateString = "Player.STATE_BUFFERING"
                    playPauseIcon = R.drawable.pause_2
                    // icyandState = "BUFFERING"
                    Observer.changesubscribenotificztion("Main text view", icyandStateWhenPlayRecordFiles("BUFFERING", ""))
                    Observer.changeText("Main text view", icyandStateWhenPlayRecordFiles("BUFFERING", ""))
                    Observer.changeText("text view", icyandStateWhenPlayRecordFiles("BUFFERING", ""))
                    changeImagePlayPause("Main image view", R.drawable.pause_2)
                    changeImagePlayPause("image view", R.drawable.pause_2)
                }
            /*  Player.STATE_READY // The player is able to immediately play from its current position.
            -> {
                  GlobalstateString = "Player.STATE_READY"
                 bufferingProgressBar.visibility = View.INVISIBLE
                  bufferingProgressBarbig.visibility = View.INVISIBLE
                 nowPlayingProgressBar.visibility = View.INVISIBLE

                  play.visibility = View.VISIBLE
                  pause.visibility = View.GONE
             }*/
                Player.STATE_ENDED // The player has finished playing the media.
                -> {
                    GlobalstateString = "Player.STATE_ENDED"
                    playPauseIcon = R.drawable.play_2
                    Observer.changesubscribenotificztion("Main text view", icyandStateWhenPlayRecordFiles("", ""))

                    Observer.changeText("Main text view", icyandStateWhenPlayRecordFiles("", ""))
                    Observer.changeText("text view", icyandStateWhenPlayRecordFiles("", ""))
                    changeImagePlayPause("Main image view", R.drawable.play_2)
                    changeImagePlayPause("image view", R.drawable.play_2)
                }
                else -> {
                    GlobalstateString = "UNKNOWN_STATE"
                /* playPauseIcon = R.drawable.play_2
                 icyandState = "OoOps! Try another station! "
                 icybackup = ""

                 Observer.changeText("Main text view", icyandState)
                 Observer.changeText("text view", icyandState)

                 changeImagePlayPause("Main image view", R.drawable.play_2)
                 changeImagePlayPause("image view", R.drawable.play_2)*/
                }
            }
            //    RadioFunction.startServices(ctx)
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            // icyandState = mediaMetadata.title.toString() //+ mediaMetadata.genre.toString()
            // icybackup = mediaMetadata.title.toString()
            icybackup = mediaMetadata.title.toString()
            RadioFunction.startServices(ctx)
            //  errorToast(ctx,"3")
            Observer.changeText("Main text view", icyandStateWhenPlayRecordFiles(mediaMetadata.title.toString(), ""))
            Observer.changeText("text view", icyandStateWhenPlayRecordFiles(mediaMetadata.title.toString(), ""))
            Observer.changesubscribenotificztion("Main text view", icyandStateWhenPlayRecordFiles(mediaMetadata.title.toString(), ""))
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)

            releasePlayer(ctx)
            if (fromAlarm) playSystemAlarm(ctx)
            RadioFunction.startServices(ctx)

            GlobalstateString = "onPlayerError"

            //   icyandState = "OoOps! Try another station! "
            //  icybackup = ""
            Observer.changesubscribenotificztion("Main text view", icyandStateWhenPlayRecordFiles("OoOps! Try another station! ", ""))

            Observer.changeText("Main text view", icyandStateWhenPlayRecordFiles("OoOps! Try another station! ", ""))
            Observer.changeText("text view", icyandStateWhenPlayRecordFiles("OoOps! Try another station! ", ""))
            changeImagePlayPause("Main image view", R.drawable.play_2)
            changeImagePlayPause("image view", R.drawable.play_2)
            playPauseIcon = R.drawable.play_2
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) = Unit
        override fun onRepeatModeChanged(repeatMode: Int) = Unit
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) = Unit
    }

    fun playSystemAlarm(context: Context) {
        // errorToast(context,defaultAlarmsoundUri.toString())

        val defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
            context,
            RingtoneManager.TYPE_ALARM
        )

        initializePlayer(context, true, defaultRingtoneUri)
        startPlayer()

        fromAlarm = false

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.alarm_backup)
            val description = context.getString(R.string.alarm_back_desc)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(ALARM_NOTIF_NAME, name, importance)
            channel.description = description
            val audioAttributes = android.media.AudioAttributes.Builder() // android.media.AudioAttributes.Builder()
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                .build()
            channel.setSound(defaultRingtoneUri, audioAttributes)

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
            NotificationCompat.Builder(context, ALARM_NOTIF_NAME)
                .setSmallIcon(R.drawable.ic_add_alarm)
                .setContentTitle(context.getString(R.string.action_alarm))
                .setContentText(context.getString(R.string.alarm_fallback_info))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSound(defaultRingtoneUri)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
        notificationManager.notify(ALARM_ID, mBuilder.build())
    }
}
