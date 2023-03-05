package com.asmtunis.player_service.service

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SimpleMediaServiceHandler @Inject constructor(
    private val player: ExoPlayer
) : Player.Listener {

    private val _simpleMediaState = MutableStateFlow<SimpleMediaState>(SimpleMediaState.Initial)
    val simpleMediaState = _simpleMediaState.asStateFlow()


    private val _icyState = MutableStateFlow<String>("")
    val icyState = _icyState.asStateFlow()




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
                    _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = true)
                    startProgressUpdate()
                }
            }
            is  PlayerEvent.Stop -> {
                stopProgressUpdate()
                player.stop()
            }
            is PlayerEvent.UpdateProgress -> player.seekTo((player.duration * playerEvent.newProgress).toLong())
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onPlaybackStateChanged(playbackState: Int) {


        when (playbackState) {
            ExoPlayer.STATE_IDLE // The player does not have any media to play.
            -> {
                _icyState.value = ""
            }
            ExoPlayer.STATE_BUFFERING // The player needs to load media before playing.
            -> {
                _icyState.value = "Buffering"
                _simpleMediaState.value = SimpleMediaState.Buffering(player.currentPosition)

            }
            ExoPlayer.STATE_READY // The player is able to immediately play from its current position.
            -> {
              //  _icyState.value = ""
                _simpleMediaState.value = SimpleMediaState.Ready(player.duration)
             }
            ExoPlayer.STATE_ENDED // The player has finished playing the media.
            -> {
                _icyState.value = ""
            }
            else -> {
              //  MainActivity.GlobalstateString = "UNKNOWN_STATE"
                _icyState.value = ""
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = isPlaying)
        if (isPlaying) {
            GlobalScope.launch(Dispatchers.Main) {
                startProgressUpdate()
            }
        } else {
            stopProgressUpdate()
        }
    }








    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)

       if(mediaMetadata.title.toString()!= "null")
           _icyState.value = mediaMetadata.title.toString()
        else if (_icyState.value == "Buffering") _icyState.value = ""


    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)

        player.stop()

    }

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
        _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = false)


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
    data class Playing(val isPlaying: Boolean) : SimpleMediaState()
}