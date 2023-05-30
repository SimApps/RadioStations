package com.amirami.simapp.radiostations.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.amirami.player_service.service.PlayerEvent
import com.amirami.player_service.service.PlayerState
import com.amirami.player_service.service.SimpleMediaServiceHandler
import com.amirami.player_service.service.SimpleMediaState
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.MainActivity.Companion.Globalurl
import com.amirami.simapp.radiostations.model.GlobalState
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.repository.RadioRoomBaseRepository
import com.amirami.simapp.radiostations.utils.Coroutines
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@UnstableApi @HiltViewModel
class SimpleMediaViewModel @Inject constructor(
    private val simpleMediaServiceHandler: SimpleMediaServiceHandler,
    //savedStateHandle: SavedStateHandle,
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



    fun getPlayer(): Player {
        return player
    }

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private val _progressString = MutableStateFlow("00:00")
    val progressString = _progressString.asStateFlow()

    private val _isPlaying = MutableStateFlow<PlayerState>(PlayerState.INITIANIAL)
    val isPlaying = _isPlaying.asStateFlow()


    private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
    val uiState = _uiState.asStateFlow()



    private val _radioState = MutableStateFlow(RadioEntity())






    // Combining these states to form a LoginState
    val state= combine( _radioState,_isPlaying) { radioState, isPlaying ->
        GlobalState(
            player = player,
            radioState= radioState,
            playerState =isPlaying,

        )
    }.stateIn(viewModelScope, WhileSubscribed(), initialValue = GlobalState())
    fun setRadioVar(radioVar : RadioEntity) {
        _radioState.value  = radioVar

    }


    init {
        viewModelScope.launch {
            simpleMediaServiceHandler.radioState.collect { icyStreamInfo ->

                 if(!_radioState.value.isRec) _radioState.value = _radioState.value.copy(

                     icyState = icyStreamInfo.icyState
                 )
                  else  _radioState.value = _radioState.value.copy(
                     name = icyStreamInfo.name,
                     icyState = _radioState.value.icyState,
                     favicon =icyStreamInfo.favicon
                  )
            }
        }

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
    fun deletelistened() {
        Coroutines.io(this@SimpleMediaViewModel) {
            favListRoomBaseRepository.deletelistened()
        }
    }

    fun upsertRadio(item: RadioEntity) {
        Coroutines.io(this@SimpleMediaViewModel) {
            if (item.name != "") { // prevent add alarm played station
                favListRoomBaseRepository.upsert(item)
            }
        }
    }

    fun loadData(radio: List<RadioEntity>) {



        setRadioVar(radio.first())


     //   setRadioVar(radio)

        Globalurl = radio[0].streamurl
        MainActivity.GlobalRadioName = radio[0].name


        if(!radio[0].isRec){
            radio[0].isLastListned = true
            radio[0].timeStamp = System.currentTimeMillis()

            upsertRadio(radio[0])

            val mediaItem = MediaItem.Builder()
                .setUri(radio[0].streamurl)
                .setMediaId(radio[0].stationuuid)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setFolderType(MediaMetadata.FOLDER_TYPE_NONE)
                       .setArtworkUri(Uri.parse(radio[0].favicon))
                      //  .setAlbumTitle(radio[0].name)


                      //  .setExtras()

                        .build()
                ).build()
            simpleMediaServiceHandler.addMediaItem(mediaItem)
        }
        else {
            val mediaItemList = mutableListOf<MediaItem>()
            (radio.indices).forEach {
                mediaItemList.add(
                    MediaItem.Builder()
                        .setUri(radio[it].streamurl)
                        .setMediaId(radio[it].stationuuid)
                        .setMediaMetadata(MediaMetadata.Builder()
                            .setFolderType(MediaMetadata.FOLDER_TYPE_PLAYLISTS)
                            .setArtworkUri(Uri.parse(radio[it].favicon))
                            .setAlbumTitle(radio[it].name)
                            .setDisplayTitle(radio[it].icyState)
                            .build()
                        ).build()
                )
            }
            simpleMediaServiceHandler.addMediaItemList(mediaItemList)
        }

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



fun com.amirami.player_service.RadioEntity.toEntity() =  RadioEntity(
    stationuuid = stationuuid,
 name = name,
 bitrate = bitrate,
 homepage = homepage,
 favicon = favicon,
 tags = tags,
 country = country,
 state = state,
 language = language,
 streamurl = streamurl,
 fav = fav,
 ip = ip,
 stationcount = stationcount,
 iso_639 = iso_639,
 moreinfo = moreinfo,
 isAlarm = isAlarm,
 isLastListned = isLastListned,
 timeStamp = timeStamp,
 icyState = icyState)