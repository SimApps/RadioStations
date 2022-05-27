package com.amirami.simapp.radiostations

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.amirami.simapp.radiostations.MainActivity.Companion.GlobalRadiourl
import com.amirami.simapp.radiostations.MainActivity.Companion.GlobalstateString
import com.amirami.simapp.radiostations.MainActivity.Companion.tag
import com.amirami.simapp.radiostations.MainActivity.Companion.downloader
import com.amirami.simapp.radiostations.MainActivity.Companion.icyandState
import com.amirami.simapp.radiostations.MainActivity.Companion.icybackup
import com.amirami.simapp.radiostations.MainActivity.Companion.video_on
import com.amirami.simapp.radiostations.Exoplayer.Observer.Companion.changeImagePlayPause
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util

import kotlin.collections.HashMap



object Exoplayer {

    lateinit var notificationManager : NotificationManager
    const val notifi_CHANNEL_ID = "SimAPPcganelIDradioApp"
    var showWhen = false
    var ongoing = false
    const val packagename="com.amirami.simapp.radiostations"
    private const val PATH = "$packagename.action."
    const val NOTIFICATION_DISMISSED = PATH + "NOTIFICATION_DISMISSED"
    const val PLAYPAUSE = PATH + "PLAYPAUSE"
    const val REC = PATH + "REC"
    const val STOP = PATH + "STOP"
    const val STOPALL = PATH + "STOPALL"
    var totalTime: Long = 0
    var is_playing_recorded_file=false
    var is_downloading=false
    var player: ExoPlayer? = null
    fun getIsPlaying() = player?.isPlaying == true
    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    var playWhenReady = true
    var audioManager: AudioManager? = null
    var mMediaSession: MediaSessionCompat? = null
    var playPauseIcon =  R.drawable.pause_2 //if (!getIsPlaying()) R.drawable.ic_pause else R.drawable.ic_play
    private val intentFilter = IntentFilter(ACTION_AUDIO_BECOMING_NOISY)
    private val myNoisyAudioStreamReceiver = BecomingNoisyReceiver()

    fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    fun initializePlayer(ctx: Context) {

        if(is_downloading) {
            downloader?.cancelDownload()
        }
        val mOreoFocusHandler: OreoAudioFocusHandler? = null
        is_playing_recorded_file=false
        icyandState =""
        icybackup=""
        showWhen = true
        ongoing = true
        val eContext = ctx.applicationContext
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentMediaItemIndex
            player!!.playWhenReady = true
            player?.stop()
            player!!.release()
            player = null

            if (isOreoPlus()) {
                mOreoFocusHandler?.abandonAudioFocus()
            } else {
                @Suppress("DEPRECATION")
                audioManager?.abandonAudioFocus(mOnAudioFocusChangeListener)
            }

            mMediaSession!!.isActive = false
            video_on=false
            releaseWifiLock()
            //GlobalstateString = "Player.STATE_IDLE"
        }
      //  GlobalstateString = "Player.STATE_BUFFERING"
        audioManager = eContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //  var focus_state = audioManager!!.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        val focusState : Int
        if (isOreoPlus()) {
            val audioFocusRequest: AudioFocusRequest?
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener)
                .setAudioAttributes(audioAttributes)
                .build()
            focusState =  audioManager?.requestAudioFocus(audioFocusRequest!!)!!
        }
        else {
            focusState =  audioManager?.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )!!
        }

        if (player == null && focusState==AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            player = ExoPlayer.Builder(eContext).build()

            player!!.playWhenReady = playWhenReady
            player!!.seekTo(currentWindow, playbackPosition)
            eContext.registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
            initMediaSession(eContext)



        }
        val mediaSource = buildMediaSource(Uri.parse(GlobalRadiourl))
        if(player!=null) {
            // player!!.setVideoDebugListener(VideoEventListener)
            player!!.addAnalyticsListener(Analyticslistener)


            //player!!.prepare(mediaSource, true, false)
            player!!.setMediaSource(mediaSource, true)
            player!!.addListener(Playerlistener)

            player!!.prepare()


            // return if media session don't work
            player!!.playbackState
           RadioFunction.startServices(eContext)
            holdWifiLock(eContext)
            //  playPauseIcon =  R.drawable.ic_pause
            // RadioFunction.startServices(eContext)
        }



    }

    fun initializeAlarmPlayer(ctx: Context) {

        if(is_downloading) {
            downloader?.cancelDownload()
        }
        val mOreoFocusHandler: OreoAudioFocusHandler? = null
        is_playing_recorded_file=false
        icyandState =""
        icybackup=""
        showWhen = true
        ongoing = true
        val eContext = ctx.applicationContext
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentMediaItemIndex
            player!!.playWhenReady = true
            player?.stop()
            player!!.release()
            player = null

            if (isOreoPlus()) {
                mOreoFocusHandler?.abandonAudioFocus()
            } else {
                @Suppress("DEPRECATION")
                audioManager?.abandonAudioFocus(mOnAudioFocusChangeListener)
            }

            mMediaSession!!.isActive = false
            video_on=false
            releaseWifiLock()
            //GlobalstateString = "Player.STATE_IDLE"
        }
       // GlobalstateString = "Player.STATE_BUFFERING"
        audioManager = eContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //  var focus_state = audioManager!!.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        val focusState : Int
        if (isOreoPlus()) {
            val audioFocusRequest: AudioFocusRequest?
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener)
                .setAudioAttributes(audioAttributes)
                .build()
            focusState =  audioManager?.requestAudioFocus(audioFocusRequest!!)!!
        }
        else {
            focusState =  audioManager?.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )!!
        }

        if (player == null && focusState==AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            player = ExoPlayer.Builder(eContext).build()

            player!!.playWhenReady = playWhenReady
            player!!.seekTo(currentWindow, playbackPosition)
            eContext.registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
            initMediaSession(eContext)



        }
        val mediaSource = buildMediaSource(Uri.parse(GlobalRadiourl))
        if(player!=null) {
            // player!!.setVideoDebugListener(VideoEventListener)
            player!!.addAnalyticsListener(Analyticslistener)


            //player!!.prepare(mediaSource, true, false)
            player!!.setMediaSource(mediaSource, true)
            player!!.addListener(Playerlistener)

            player!!.prepare()


            // return if media session don't work
            player!!.playbackState
          //  RadioFunction.startServices(eContext)
            holdWifiLock(eContext)
            //  playPauseIcon =  R.drawable.ic_pause
            // RadioFunction.startServices(eContext)
        }



    }

    /*   val client = OkHttpClient.Builder().build()
       val icyHttpDataSourceFactory = IcyHttpDataSourceFactory.Builder(client)
          // .setUserAgent(userAgent)
           .setIcyHeadersListener { icyHeaders ->
               icy += icyHeaders.toString()
               Observer.changeText("Main text view", icy)
                   //  RadioFunction.startServices(eContext)
               Log.d("XXX", "onIcyHeaders: %s".format(icyHeaders.toString()))
           }
           .setIcyMetadataChangeListener { icyMetadata ->
               icy +=icyMetadata.toString()
               Observer.changeText("Main text view", icy)
             //  RadioFunction.startServices(eContext)
               Log.d("XXX", "onIcyMetaData: %s".format(icyMetadata.toString()))
           }
           .build()*/
    fun initializePlayerRecodedradio(ctx: Context){
        val mOreoFocusHandler: OreoAudioFocusHandler? = null

        if(is_downloading) {
            downloader?.cancelDownload()
        }

        is_playing_recorded_file=true
        //intialise player begin
        icyandState =""
        icybackup=""
        showWhen = true
        ongoing = true
        val eContext = ctx.applicationContext
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentMediaItemIndex
            player!!.playWhenReady = true
            player?.stop()
            player!!.release()
            player = null

            if (isOreoPlus()) {
                mOreoFocusHandler?.abandonAudioFocus()
            } else {
                @Suppress("DEPRECATION")
                audioManager?.abandonAudioFocus(mOnAudioFocusChangeListener)
            }

            mMediaSession!!.isActive = false
            video_on =false
            releaseWifiLock()
         //   GlobalstateString = "Player.STATE_IDLE"
        }
        //GlobalstateString = "Player.STATE_BUFFERING"
        audioManager = eContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //  var focus_state = audioManager!!.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

        val focusState : Int
        if (isOreoPlus()) {
            val audioFocusRequest: AudioFocusRequest?
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener)
                .setAudioAttributes(audioAttributes)
                .build()

            focusState =  audioManager?.requestAudioFocus(audioFocusRequest!!)!!

            //  focus_state = mOreoFocusHandler?.requestAudioFocus(mOnAudioFocusChangeListener)
        }
        else {
            focusState =  audioManager?.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )!!
        }
        if (player == null && focusState==AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            player = ExoPlayer.Builder(ctx).build()




            player!!.playWhenReady = playWhenReady
            player!!.seekTo(currentWindow, playbackPosition)
            ctx.registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
            initMediaSession(ctx)
        }

        val mediaItem =  MediaItem.fromUri(Uri.parse(GlobalRadiourl))
        val mediaSource = ProgressiveMediaSource
            .Factory( DefaultDataSource.Factory (ctx))
            .createMediaSource(mediaItem)

        player!!.setMediaSource(mediaSource)
        player!!.addListener(Playerlistener)
        player!!.prepare()
        player!!.repeatMode = Player.REPEAT_MODE_OFF
        player!!.playbackState
       RadioFunction.startServices(ctx)
        holdWifiLock(ctx)

        //   playPauseIcon =  R.drawable.ic_pause
        // initialise player end
    }

    class Observer private constructor() {
        private val map:HashMap<String, TextView> = HashMap()
        private val mapnotify:HashMap<String, String> = HashMap()
        private val mapimage:HashMap<String, ImageView> = HashMap()
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
                if (observer.map.containsKey(viewKey))
                {
                    val textView = observer.map[viewKey]
                    textView!!.text = text

                }
                /*  else
                  {
                      // throw exception
                  }*/
            }
            fun changeText(viewKey: String, text: String) {
                val observer = instance
                if (observer.map.containsKey(viewKey))
                {
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
                if (observer.mapimage.containsKey(viewKey))
                {
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
                if (observer.mapimage.containsKey(viewKey))
                {
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

    fun releasePlayer() {
        val mOreoFocusHandler: OreoAudioFocusHandler? = null

        if (player != null) {

            if(is_downloading){
                Observer.changeImageRecord("Main stop image view", R.drawable.stop_2)

                downloader?.cancelDownload()

            }
            else{
                player!!.removeListener(Playerlistener)
                playbackPosition = player!!.currentPosition
                currentWindow = player!!.currentMediaItemIndex
                player!!.playWhenReady = true
                player!!.playbackState
                player?.stop()
                player!!.release()
              //  GlobalstateString = "Player.STATE_IDLE"
                player = null
                if (isOreoPlus()) {
                    mOreoFocusHandler?.abandonAudioFocus()
                } else {
                    @Suppress("DEPRECATION")
                    audioManager?.abandonAudioFocus(mOnAudioFocusChangeListener)
                }
                mMediaSession!!.isActive = false
                video_on=false
                releaseWifiLock()
                icyandState =""
                Observer.changeText("Main text view", icyandState)
                Observer.changeText("text view", icyandState)

                playPauseIcon = R.drawable.play_2
                changeImagePlayPause("Main image view", R.drawable.play_2)
                changeImagePlayPause("image view", R.drawable.play_2)
            }
        }
    }

    fun releaseAlarmPlayer() {
        val mOreoFocusHandler: OreoAudioFocusHandler? = null

        if (player != null) {

                player!!.removeListener(Playerlistener)
                playbackPosition = player!!.currentPosition
                currentWindow = player!!.currentMediaItemIndex
                player!!.playWhenReady = true
                player!!.playbackState
                player?.stop()
                player!!.release()
               GlobalstateString = "Player.STATE_IDLE"
                player = null
                if (isOreoPlus()) {
                    mOreoFocusHandler?.abandonAudioFocus()
                } else {
                    @Suppress("DEPRECATION")
                    audioManager?.abandonAudioFocus(mOnAudioFocusChangeListener)
                }
                mMediaSession!!.isActive = false
                video_on=false
                releaseWifiLock()

        }
    }
    private fun buildMediaSource(uri: Uri): MediaSource {

        val type = Util.inferContentType(uri)
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            //  .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
        return when (type) {

            C.TYPE_DASH -> {
                video_on = true

                val dashChunkSourceFactory = DefaultDashChunkSource.Factory(
                    //  icyHttpDataSourceFactory
                    DefaultHttpDataSource.Factory()
                )


                val manifestDataSourceFactory =  DefaultHttpDataSource.Factory()
                DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
                    .createMediaSource(/*uri*/
                        mediaItem
                    )

            }
          /*  C.TYPE_SS -> {
                video_on = true
                val ssSourceFactory = DefaultSsChunkSource.Factory(
                    //   icyHttpDataSourceFactory
                    DefaultHttpDataSource.Factory()
                )
                val manifestDataSourceFactory =  DefaultHttpDataSource.Factory()
                SsMediaSource.Factory(ssSourceFactory, manifestDataSourceFactory).createMediaSource(
                    mediaItem
                )
            }*/
            C.TYPE_HLS -> {
                video_on = true
                HlsMediaSource.Factory( // icyHttpDataSourceFactory
                    DefaultHttpDataSource.Factory()
                )
                    .createMediaSource(mediaItem)
            }
            C.TYPE_OTHER -> {
                ProgressiveMediaSource.Factory(
                    DefaultHttpDataSource.Factory()
                    // icyHttpDataSourceFactory
                )
                    .createMediaSource(mediaItem)
            }
            else ->{
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }

    fun pausePlayer(context: Context ) {
        Observer.changeText("Main text view", icyandState)
        Observer.changeText("text view", icyandState)
        if (player!=null){
            player!!.playWhenReady = false
            player!!.playbackState
        }

        //GlobalstateString="Player.STATE_PAUSED"

        if(is_downloading){
            Observer.changeImageRecord("Main stop image view", R.drawable.stop_2)
            downloader?.cancelDownload()
        }

        RadioFunction.startServices(context)
    }

    fun startPlayer() {
        if (player!=null){
            player!!.playWhenReady = true
            player!!.playbackState
            mMediaSession!!.isActive = true


        }

    }

    private fun soundPlayer(raw_id: Float) {
        if(player!=null){
            player!!.volume =raw_id
        }
    }

    //Audio Focus
    private val mOnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.i(tag, "AUDIOFOCUS_GAIN")

                if (player != null) {
                    if (player!!.playWhenReady) {
                        soundPlayer(1f)
                        startPlayer()
                    }
                }

            }
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> {
                Log.i(tag, "AUDIOFOCUS_GAIN_TRANSIENT")
                // You have audio focus for a short time
                startPlayer()
            }
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> {
                Log.i(tag, "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK")
                // Play over existing audio
                soundPlayer(/*ctx: Context, raw_id: Int*/0.25f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                Log.e(tag, "AUDIOFOCUS_LOSS")

                releasePlayer()

            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Log.e(tag, "AUDIOFOCUS_LOSS_TRANSIENT")
                // Temporary loss of audio focus - expect to get it back - you can keep your resources around
                soundPlayer(0.01f)
                if(player!=null){
                    player!!.playbackState

                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Log.e(tag, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                soundPlayer(0.25f)
                // Lower the volume
            }
        }
    }

    //pause when headphone off
    class BecomingNoisyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_AUDIO_BECOMING_NOISY) {
                // Pause the playback
                if(player!=null){
                    pausePlayer(context)

                }
            }
        }
    }

    // handel MediaSession

    fun initMediaSession(ctx: Context) {
       mMediaSession = MediaSessionCompat(ctx, packagename)
        mMediaSession!!.release()

        if (!mMediaSession!!.isActive) {
            //Use Media Session Connector from the EXT library to enable MediaSession Controls in PIP.
            val mediaSessionConnector = MediaSessionConnector(mMediaSession!!)
           mediaSessionConnector.setPlayer(player/*, null*/)
            mMediaSession!!.isActive = true
        }

    }

    object Analyticslistener: AnalyticsListener
    // create a class member variable.
    private var mWifiLock: WifiManager.WifiLock?=null
    /***
     * Calling this method will aquire the lock on wifi. This is avoid wifi
     * from going to sleep as long as <code>releaseWifiLock</code> method is called.
     **/
    private fun holdWifiLock(ctx: Context) {
        val wifiManager = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (mWifiLock == null)
            mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, tag)
        mWifiLock!!.setReferenceCounted(false)
        if (!mWifiLock!!.isHeld)
            mWifiLock!!.acquire()
    }
    /***
     * Calling this method will release if the lock is already help. After this method is called,
     * the Wifi on the device can goto sleep.
     **/
    private fun releaseWifiLock() {

        if (mWifiLock == null)
            Log.w(tag, "#releaseWifiLock mWifiLock was not created previously")
        if (mWifiLock != null && mWifiLock!!.isHeld)
        {
            mWifiLock!!.release()
            //mWifiLock = null;
        }
    }


//DefaultEventListener


    object Playerlistener :Player.Listener {
   /*      override fun  onPlaybackStateChanged(playbackState: Int){

        }

        override fun  onPlayWhenReadyChanged(playWhenReady: Boolean, playbackState: Int){
            Exoplayer.playWhenReady=playWhenReady

        }


        override fun onIsPlayingChanged(isPlaying: Boolean) {

            ff = isPlaying.toString()
        }*/
       override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

           if (playWhenReady && playbackState == Player.STATE_READY)
           {
               if(is_playing_recorded_file){
                   totalTime = player?.duration!!
               }

               playPauseIcon =  R.drawable.pause_2
               GlobalstateString = "Player.STATE_READY"
               changeImagePlayPause("Main image view", R.drawable.pause_2)
               changeImagePlayPause("image view", R.drawable.pause_2)

               if(icyandState == "BUFFERING"){
                   icyandState=""
                   Observer.changeText("Main text view", icybackup)
                   Observer.changeText("text view", icybackup)
               }
           }

           else if (playWhenReady) {
                 when (playbackState) {
                     Player.STATE_IDLE // The player does not have any media to play.
                     -> {

                         playPauseIcon = R.drawable.play_2
                         GlobalstateString = "Player.STATE_IDLE"
                         changeImagePlayPause("Main image view", R.drawable.play_2)
                         changeImagePlayPause("image view", R.drawable.play_2)


                     }
                     Player.STATE_BUFFERING // The player needs to load media before playing.
                     -> {
                         GlobalstateString = "Player.STATE_BUFFERING"
                         playPauseIcon = R.drawable.pause_2
                         icyandState = "BUFFERING"
                         Observer.changeText("Main text view", icyandState)
                         Observer.changeText("text view", icyandState)
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
                         changeImagePlayPause("Main image view", R.drawable.play_2)
                         changeImagePlayPause("image view", R.drawable.play_2)
                     }
                     else -> {
                         GlobalstateString = "UNKNOWN_STATE"
                         playPauseIcon =  R.drawable.play_2
                         icyandState="OoOps! Try another station! "
                         icybackup=""

                         Observer.changeText("Main text view", icyandState)
                         Observer.changeText("text view", icyandState)

                         changeImagePlayPause("Main image view", R.drawable.play_2)
                         changeImagePlayPause("image view", R.drawable.play_2)
                     }
                 }
             }
           else {
                  playPauseIcon =  R.drawable.play_2
                  GlobalstateString="Player.STATE_PAUSED"
                  changeImagePlayPause("Main image view", R.drawable.play_2)
                  changeImagePlayPause("image view", R.drawable.play_2)
              }
        }
        //override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) = Unit
   //     override fun onTracksChanged(groups: TrackGroupArray, selections: TrackSelectionArray) = Unit
    //    override fun onLoadingChanged(isLoading: Boolean) = Unit
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {

            super.onMediaMetadataChanged(mediaMetadata)
            icyandState =mediaMetadata.title.toString() //+ mediaMetadata.genre.toString()
            icybackup=mediaMetadata.title.toString()
            Observer.changeText("Main text view", icyandState)
            Observer.changeText("text view", icyandState)
            Observer.changesubscribenotificztion("Main text view", icyandState)

        }
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)


            Log.e(tag, "onPlayerStateChanged: error=$error")
            GlobalstateString="onPlayerError"
            releasePlayer()
            icyandState="OoOps! Try another station! "
            icybackup=""
            Observer.changeText("Main text view", icyandState)
            Observer.changeText("text view", icyandState)
            changeImagePlayPause("Main image view", R.drawable.play_2)
            changeImagePlayPause("image view", R.drawable.play_2)
            playPauseIcon =  R.drawable.play_2

        }
      //  override fun onPositionDiscontinuity(reason: Int) = Unit
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) = Unit
      //  override fun onSeekProcessed() = Unit
        override fun onRepeatModeChanged(repeatMode: Int) = Unit
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) = Unit
    }

    /*
       private var mClicksCnt = 0
     private val mRemoteControlHandler = Looper.myLooper()?.let { Handler(it) }
    private const val MAX_CLICK_DURATION = 700L

    private val mRunnable = Runnable {
        if (mClicksCnt == 0) {
            return@Runnable
        }

        when (mClicksCnt) {
            1 -> {
                if (player!=null){
                    if (player!!.playWhenReady) {
                        pausePlayer()

                    } else {
                        startPlayer()
                    }
                }

            }
            2 -> {
                releasePlayer()
            }
            else -> {
                releasePlayer()
            }
        }
        mClicksCnt = 0
    }

     fun handleMediaButton(mediaButtonEvent: Intent, ctx: Context) {
        if (mediaButtonEvent.action == Intent.ACTION_MEDIA_BUTTON) {
          //  val swapPrevNext = config.swapPrevNext
            val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
            if (event!!.action == KeyEvent.ACTION_UP) {
                when (event.keyCode) {
                    KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                        if (player != null) {
                        if (player!!.playWhenReady) {
                            pausePlayer()
                            if (is_downloading) {
                                downloader?.cancelDownload()
                            }
                        } else {
                            startPlayer()
                        }

                            RadioFunction.startServices(ctx)
                        }
                    }
                    //   KeyEvent.KEYCODE_MEDIA_PREVIOUS -> if (swapPrevNext) handleNext() else handlePrevious()
                    //  KeyEvent.KEYCODE_MEDIA_NEXT -> if (swapPrevNext) handlePrevious() else handleNext()
                    KeyEvent.KEYCODE_HEADSETHOOK -> {
                        mClicksCnt++
                        mRemoteControlHandler!!.removeCallbacks(mRunnable)
                        if (mClicksCnt >= 3) {
                            mRemoteControlHandler.post(mRunnable)
                        } else {
                            mRemoteControlHandler.postDelayed(mRunnable, MAX_CLICK_DURATION)
                        }
                    }

                }
            }
        }
    }
*/

}

