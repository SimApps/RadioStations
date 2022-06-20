package com.amirami.simapp.radiostations

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amirami.simapp.radiostations.Exoplayer.PLAYPAUSE
import com.amirami.simapp.radiostations.Exoplayer.STOP
import com.amirami.simapp.radiostations.Exoplayer.STOPALL
import com.amirami.simapp.radiostations.Exoplayer.isOreoPlus
import com.amirami.simapp.radiostations.Exoplayer.is_downloading

class ControlActionsListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        fun handlePlayPause() {
            if (Exoplayer.playWhenReady && Exoplayer.getIsPlaying) Exoplayer.pausePlayer()
            else {
                if(Exoplayer.player ==null){
                    if(Exoplayer.is_playing_recorded_file) Exoplayer.initializePlayer(context, true)
                    else Exoplayer.initializePlayer(context, false)
                }
                Exoplayer.startPlayer()
            }
        }

        fun Context.playpauseIntent(action: String) {
            handlePlayPause()
            Intent(this, NotificationChannelService::class.java).apply {
                this.action = action
                    try {
                        if (isOreoPlus()) startForegroundService(this)
                        else startService(this)
                    } catch (ignored: Exception) { }

                }
        }

        fun Context.stopallIntent(action: String) {
            Exoplayer.releasePlayer(context)
            Intent(this, NotificationChannelService::class.java).apply {
                this.action = action
                try {
                    if(!is_downloading)  stopService(this)
                } catch (ignored: Exception) { }
            }

        }

        fun Context.stopIntent(action: String) {
            Exoplayer.releasePlayer(context)
            Intent(this, NotificationChannelService::class.java).apply {
                this.action = action
            }

        }
        when (val action = intent.action) {
            PLAYPAUSE  -> context.playpauseIntent(action)
            STOP -> context.stopIntent(action)
            STOPALL -> context.stopallIntent(action)
        }


    }


}
