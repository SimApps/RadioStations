package com.amirami.simapp.radiostations.model

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

data class GlobalState (
  val isRecFile: Boolean = false,
  val player: ExoPlayer? = null,
  val radioState: RadioEntity = RadioEntity(),
  val isPlaying: Boolean = false,
)