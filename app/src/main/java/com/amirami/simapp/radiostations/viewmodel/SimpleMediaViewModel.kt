package com.amirami.simapp.radiostations.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.amirami.simapp.radiostations.Exoplayer
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.MainActivity.Companion.Globalurl
import com.amirami.simapp.radiostations.MainActivity.Companion.icyandState
import com.amirami.simapp.radiostations.MainActivity.Companion.is_playing_recorded_file
import com.amirami.simapp.radiostations.R
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.model.Resource
import com.amirami.simapp.radiostations.model.Status
import com.amirami.simapp.radiostations.repository.RadioRoomBaseRepository
import com.amirami.simapp.radiostations.utils.Coroutines
import com.asmtunis.player_service.service.PlayerEvent
import com.asmtunis.player_service.service.SimpleMediaServiceHandler
import com.asmtunis.player_service.service.SimpleMediaState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SimpleMediaViewModel @Inject constructor(
    private val simpleMediaServiceHandler: SimpleMediaServiceHandler,
    savedStateHandle: SavedStateHandle,
    private val player: ExoPlayer,
    private val favListRoomBaseRepository: RadioRoomBaseRepository
) : ViewModel() {

 /*
 var duration by savedStateHandle.saveable { mutableStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableStateOf(0f) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }

  */
 //private val videoUris: StateFlow<List<Uri>> = savedStateHandle.getStateFlow(VIDEO_URIS, emptyList())



    fun getPlayer(): Player? {
        return player
    }

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private val _progressString = MutableStateFlow("00:00")
    val progressString = _progressString.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
    val uiState = _uiState.asStateFlow()

    private val _icyStreamInfoState = MutableStateFlow("")
    val icyStreamInfoState = _icyStreamInfoState.asStateFlow()


    private val _radioVar = MutableStateFlow(RadioEntity())
    val radioVar = _radioVar.asStateFlow()



    private val _isRecFile = MutableStateFlow(false)
    val isRecFile = _isRecFile.asStateFlow()

    fun setRadioVar(radioVar : RadioEntity) {
        _radioVar.value  = radioVar
    }
    init {
        viewModelScope.launch {

            simpleMediaServiceHandler.simpleMediaState.collect { mediaState ->
                when (mediaState) {
                    is SimpleMediaState.Buffering -> calculateProgressValues(mediaState.progress)
                    is SimpleMediaState.Initial -> _uiState.value = UIState.Initial
                    is SimpleMediaState.Playing -> _isPlaying.value = mediaState.isPlaying
                    is SimpleMediaState.Progress -> calculateProgressValues(mediaState.progress)
                    is SimpleMediaState.Ready -> {
                        _duration.value =  mediaState.duration
                        //duration =  mediaState.duration
                        _uiState.value = UIState.Ready

                    }
                }
            }


        }
        viewModelScope.launch {
            simpleMediaServiceHandler.icyState.collect { icyStreamInfo ->
                icyandState = icyStreamInfo
            _icyStreamInfoState.value = icyStreamInfo

            }
        }

    }

    override fun onCleared() {
        viewModelScope.launch {
            simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
        }
    }

    fun onUIEvent(uiEvent: UIEvent) = viewModelScope.launch {
        when (uiEvent) {
            is UIEvent.Backward -> simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Backward)
            is UIEvent.Forward -> simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Forward)
            is UIEvent.PlayPause -> {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.PlayPause)
            }
            is UIEvent.UpdateProgress -> {
                _progress.value = uiEvent.newProgress
                simpleMediaServiceHandler.onPlayerEvent(
                    PlayerEvent.UpdateProgress(
                        uiEvent.newProgress
                    )
                )
            }

            is  UIEvent.Stop ->  viewModelScope.launch {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
            }
        }
    }


    fun formatDuration(duration: Long): String {
        val minutes: Long = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds: Long = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%02d:%02d", minutes, seconds)
    }



    private fun calculateProgressValues(currentProgress: Long) {
        _progress.value = if (currentProgress > 0) (currentProgress.toFloat() / _duration.value) else 0f
        _progressString.value = formatDuration(currentProgress)
    }
    fun deletelistened(fav: Boolean) {
        Coroutines.io(this@SimpleMediaViewModel) {
            favListRoomBaseRepository.deletelistened(fav)
        }
    }

    fun upsertRadio(item: RadioEntity) {
        Coroutines.io(this@SimpleMediaViewModel) {
            if (item.name != "") { // prevent add alarm played station
                favListRoomBaseRepository.upsert(item)
            }
        }
    }

    fun loadData(radio : RadioEntity, isRec : Boolean = false) {
        _isRecFile.value = isRec
         is_playing_recorded_file = isRec
        _radioVar.value = radio

        Globalurl = radio.streamurl
        MainActivity.GlobalRadioName = radio.name
        upsertRadio(radio)
        deletelistened(false)
        val mediaItem = MediaItem.Builder()
            .setUri(radio.streamurl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
                    .setArtworkUri(Uri.parse(radio.favicon))
                    .setAlbumTitle(radio.name)
                    .setDisplayTitle(_icyStreamInfoState.value)
                    .build()
            ).build()

        //val mediaItemList = mutableListOf<MediaItem>()
        //(1..17).forEach {
        //    mediaItemList.add(
        //        MediaItem.Builder()
        //            .setUri("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-$it.mp3")
        //            .setMediaMetadata(MediaMetadata.Builder()
        //                .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
        //                .setArtworkUri(Uri.parse("https://cdns-images.dzcdn.net/images/cover/1fddc1ab0535ee34189dc4c9f5f87bf9/264x264.jpg"))
        //                .setAlbumTitle("SoundHelix")
        //                .setDisplayTitle("Song $it")
        //                .build()
        //            ).build()
        //    )
        //}

        simpleMediaServiceHandler.addMediaItem(mediaItem)
        //simpleMediaServiceHandler.addMediaItemList(mediaItemList)

         onUIEvent(UIEvent.PlayPause)

    }

}

sealed class UIEvent {
    object PlayPause : UIEvent()

    object Stop : UIEvent()
    object Backward : UIEvent()
    object Forward : UIEvent()
    data class UpdateProgress(val newProgress: Float) : UIEvent()
}

sealed class UIState {
    object Initial : UIState()
    object Ready : UIState()
}