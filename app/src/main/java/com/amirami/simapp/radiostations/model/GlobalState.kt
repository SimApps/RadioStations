package com.amirami.simapp.radiostations.model

import androidx.media3.exoplayer.ExoPlayer
import com.amirami.player_service.service.PlayerState

data class GlobalState (
  val isRecFile: Boolean = false,
  val player: ExoPlayer? = null,
  val radioState: RadioEntity = RadioEntity(),
  val playerState: PlayerState = PlayerState.INITIANIAL
)