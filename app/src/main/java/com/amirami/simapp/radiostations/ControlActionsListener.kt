package com.amirami.simapp.radiostations

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amirami.simapp.radiostations.Exoplayer.PLAYPAUSE
import com.amirami.simapp.radiostations.Exoplayer.STOP
import com.amirami.simapp.radiostations.Exoplayer.STOPALL
import com.amirami.simapp.radiostations.Exoplayer.isOreoPlus

class ControlActionsListener : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        fun Context.sendIntent(action: String) {
            Intent(this, NotificationChannelService::class.java).apply {
                this.action = action
                try {
                   if (isOreoPlus()) startForegroundService(this)
                    else startService(this)
                } catch (ignored: Exception) { }
            }

        }

        when (val action = intent.action) {
             PLAYPAUSE , STOP, STOPALL -> context.sendIntent(action)

        }
    }
}
