package com.asmtunis.player_service.service.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import coil.Coil
import coil.request.ImageRequest


@UnstableApi
class SimpleMediaNotificationAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?
) : PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player): CharSequence =
        player.mediaMetadata.albumTitle ?: ""

    override fun createCurrentContentIntent(player: Player): PendingIntent? =
        pendingIntent

    override fun getCurrentContentText(player: Player): CharSequence =
        player.mediaMetadata.displayTitle ?: ""

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {

     val imageLoader = Coil.imageLoader(context) // or create your own instance

        val request = ImageRequest.Builder(context)
            .data(player.mediaMetadata.artworkUri)
            .target { drawable ->
                // Handle the result.
                val bitmap = (drawable as BitmapDrawable).bitmap
                callback.onBitmap(bitmap)
            }
            .build()
        imageLoader.enqueue(request)
        return null
    }

}
