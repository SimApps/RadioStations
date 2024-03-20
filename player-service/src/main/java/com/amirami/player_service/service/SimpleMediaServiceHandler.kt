package com.amirami.player_service.service

import android.annotation.SuppressLint
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.amirami.player_service.RadioEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SimpleMediaServiceHandler @Inject constructor(
    private val player: ExoPlayer
) : Player.Listener {

    private val _simpleMediaState = MutableStateFlow<SimpleMediaState>(SimpleMediaState.Initial)
    val simpleMediaState = _simpleMediaState.asStateFlow()


    private val _radioState = MutableStateFlow(RadioEntity())
    val radioState = _radioState.asStateFlow()

     



    private var job: Job? = null

    init {
        player.addListener(this)
        job = Job()
    }

    fun addMediaItem(mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun addMediaItemList(mediaItemList: List<MediaItem>) {
        player.setMediaItems(mediaItemList)
        player.prepare()
    }

    suspend fun onPlayerEvent(playerEvent: PlayerEvent) {
        when (playerEvent) {
            is PlayerEvent.Backward -> player.seekBack()
            is PlayerEvent.Forward -> player.seekForward()
            is PlayerEvent.PlayPause -> {
                if (player.isPlaying) {
                    player.pause()
                    stopProgressUpdate()



                } else {
                    //player.prepare()
                    player.play()
                    startProgressUpdate()

                }
            }
            is  PlayerEvent.Stop -> {

                stopProgressUpdate()
                player.stop()

                _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = PlayerState.STOPED)


            }
            is PlayerEvent.UpdateProgress -> player.seekTo((player.duration * playerEvent.newProgress).toLong())
        }
    }
    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        player.stop()
        //player.release()

        _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = PlayerState.STOPED)

        _radioState.value =    _radioState.value.copy(icyState="OoOps! Try another station!")


    }
    @SuppressLint("SwitchIntDef")
    override fun onPlaybackStateChanged(playbackState: Int) {


        when (playbackState) {
            ExoPlayer.STATE_IDLE // The player does not have any media to play.
            -> {
                _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = PlayerState.STOPED)
                if(_radioState.value.icyState !="OoOps! Try another station!")
                    _radioState.value =     _radioState.value.copy(icyState="")

            }
            ExoPlayer.STATE_BUFFERING // The player needs to load media before playing.
            -> {
                _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = PlayerState.BUFFERING)
                _radioState.value =   _radioState.value.copy(icyState="Buffering")

                _simpleMediaState.value = SimpleMediaState.Buffering(player.currentPosition)

            }
            ExoPlayer.STATE_READY // The player is able to immediately play from its current position.
            -> {
                _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = PlayerState.PLAYING)
              //  _icyState.value = ""
                if(_radioState.value.icyState =="Buffering")
                _radioState.value =   _radioState.value.copy(icyState="")
                _simpleMediaState.value = SimpleMediaState.Ready(player.duration)
             }
            ExoPlayer.STATE_ENDED // The player has finished playing the media.
            -> {
                _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = PlayerState.STOPED)
                _radioState.value =   _radioState.value.copy(icyState="")

            }
            else -> {
                _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = PlayerState.STOPED)

              //  MainActivity.GlobalstateString = "UNKNOWN_STATE"
                _radioState.value =   _radioState.value.copy(icyState="")
            }
        }
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        _radioState.value =   _radioState.value.copy(
            favicon = mediaMetadata.artworkUri.toString(),
            name = mediaMetadata.albumTitle.toString(),
            icyState=mediaMetadata.title.toString()
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.d("ikjnhbg"," vvvvv " +  simpleMediaState.value.toString())

        if (isPlaying) {
            GlobalScope.launch(Dispatchers.Main) {
                _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = PlayerState.PLAYING)

                startProgressUpdate()
            }
        } else {
            _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = PlayerState.PAUSED)

            stopProgressUpdate()
        }
        Log.d("ikjnhbg"," ing " +  _simpleMediaState.value.toString())

    }


/*
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
 */








    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) = Unit
    override fun onRepeatModeChanged(repeatMode: Int) = Unit
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) = Unit
    private suspend fun startProgressUpdate() = job.run {
        while (true) {
            delay(500)
            _simpleMediaState.value = SimpleMediaState.Progress(player.currentPosition)
        }
    }



    private fun stopProgressUpdate() {
        job?.cancel()


    }


}

sealed class PlayerEvent {
    object PlayPause : PlayerEvent()
    object Backward : PlayerEvent()
    object Forward : PlayerEvent()
    object Stop : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
}

sealed class SimpleMediaState {
    object Initial : SimpleMediaState()
    data class Ready(val duration: Long) : SimpleMediaState()
    data class Progress(val progress: Long) : SimpleMediaState()
    data class Buffering(val progress: Long) : SimpleMediaState()
    data class Playing(val isPlaying: PlayerState) : SimpleMediaState()
 }

enum class PlayerState {
    PLAYING, STOPED, PAUSED, INITIANIAL, BUFFERING
}