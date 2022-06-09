package com.amirami.simapp.radiostations

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class NotificationDismissedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Intent(context, NotificationChannelService::class.java).apply {
          /*  if(Exoplayer.player!=null){
                Exoplayer.player!!.removeListener(Exoplayer.playbackStateListener(context))
            }*/
            context.stopService(this)

        }
    }
}
