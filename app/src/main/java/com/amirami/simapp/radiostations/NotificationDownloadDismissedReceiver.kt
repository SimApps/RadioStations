package com.amirami.simapp.radiostations

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.media3.common.util.UnstableApi

@UnstableApi class NotificationDownloadDismissedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Intent(context, NotificationChannelService::class.java).apply {
            if (Exoplayer.player != null && Exoplayer.is_downloading) MainActivity.downloader?.cancelDownload()
        }
    }
}
