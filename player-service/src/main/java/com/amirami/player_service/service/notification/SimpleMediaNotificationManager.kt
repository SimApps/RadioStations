package com.amirami.player_service.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.ui.PlayerNotificationManager
import com.asmtunis.player_service.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val NOTIFICATION_ID = 909090909

@RequiresApi(Build.VERSION_CODES.O)
class SimpleMediaNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val player: ExoPlayer
) {

    private var notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    @UnstableApi
    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ) {
        buildNotification(mediaSession)
        startForegroundNotification(mediaSessionService)
    }

    @UnstableApi
    private fun buildNotification(mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(context, NOTIFICATION_ID, context.packageName+"ID")
            .setMediaDescriptionAdapter(
                SimpleMediaNotificationAdapter(
                    context = context,
                    pendingIntent = mediaSession.sessionActivity
                )
            )
            //.setSmallIconResourceId(R.drawable.media3_icon_circular_play)
            .setStopActionIconResourceId(R.drawable.stop_2)
            .setPlayActionIconResourceId(R.drawable.play_2)
            .setPauseActionIconResourceId(R.drawable.pause_2)
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.sessionCompatToken)
                it.setUseFastForwardActionInCompactView(true)
                it.setUseRewindActionInCompactView(true)
                it.setUseNextActionInCompactView(false)
                it.setPriority(NotificationCompat.PRIORITY_LOW)
                it.setUseStopAction(true)

                it.setPlayer(player)
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundNotification(mediaSessionService: MediaSessionService) {
        val notification = Notification.Builder(context, context.packageName+"ID")
           .setCategory(Notification.CATEGORY_SERVICE)
            // .setCategory(Notification.CATEGORY_EVENT)
            .build()
        mediaSessionService.startForeground(NOTIFICATION_ID, notification)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            context.packageName+"ID",
            context.packageName,
           // NotificationManager.IMPORTANCE_HIGH
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}

