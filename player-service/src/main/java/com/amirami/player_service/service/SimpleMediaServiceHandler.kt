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


    private val _radioState = MutableStateFlow<RadioEntity>(RadioEntity())
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
    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Log.d("jnppllk","OoOps! Try another station!")
        player.stop()
        //player.release()

        _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = false)

        _radioState.value =    _radioState.value.copy(icyState="OoOps! Try another station!")


    }
    @SuppressLint("SwitchIntDef")
    override fun onPlaybackStateChanged(playbackState: Int) {


        when (playbackState) {
            ExoPlayer.STATE_IDLE // The player does not have any media to play.
            -> {
                Log.d("jnppllk","1")

                if(_radioState.value.icyState !="OoOps! Try another station!")
                    _radioState.value =     _radioState.value.copy(icyState="")

            }
            ExoPlayer.STATE_BUFFERING // The player needs to load media before playing.
            -> {
                Log.d("jnppllk","2")
                _radioState.value =   _radioState.value.copy(icyState="Buffering")

                _simpleMediaState.value = SimpleMediaState.Buffering(player.currentPosition)

            }
            ExoPlayer.STATE_READY // The player is able to immediately play from its current position.
            -> {
                Log.d("jnppllk","3")
              //  _icyState.value = ""
                if(_radioState.value.icyState =="Buffering")
                _radioState.value =   _radioState.value.copy(icyState="")
                _simpleMediaState.value = SimpleMediaState.Ready(player.duration)
             }
            ExoPlayer.STATE_ENDED // The player has finished playing the media.
            -> {
                Log.d("jnppllk","4")
                _radioState.value =   _radioState.value.copy(icyState="")

            }
            else -> {
                Log.d("jnppllk","5")
              //  MainActivity.GlobalstateString = "UNKNOWN_STATE"
                _radioState.value =   _radioState.value.copy(icyState="")
            }
        }
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        Log.d("jnppllk","cccc "+_radioState.value.icyState)
        Log.d("jnppllk","cccccc "+mediaMetadata.title.toString())

        if(mediaMetadata.title.toString()!= "null"){
            Log.d("jnppllk","s "+mediaMetadata.title.toString())
            Log.d("jnppllk","nn "+mediaMetadata.albumTitle.toString())
            _radioState.value =   _radioState.value.copy(
                name = mediaMetadata.albumTitle.toString(),
                icyState=mediaMetadata.title.toString()
            )
        }




        else /*if (_radioState.value.icyState == "Buffering")*/{
            Log.d("jnppllk","sq "+mediaMetadata.title.toString())
            Log.d("jnppllk","nnq "+mediaMetadata.albumTitle.toString())
            _radioState.value =   _radioState.value.copy(
                name = mediaMetadata.albumTitle.toString())
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