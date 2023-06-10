package com.amirami.simapp.radiobroadcast.model

import androidx.media3.exoplayer.ExoPlayer
import com.amirami.player_service.service.PlayerState

data class GlobalState (
  val player: ExoPlayer? = null,
  val radioState: RadioEntity = RadioEntity(),
  val playerState: PlayerState = PlayerState.INITIANIAL
)