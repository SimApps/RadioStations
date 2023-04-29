package com.amirami.player_service.service.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import coil.Coil
import coil.request.ImageRequest
import com.asmtunis.player_service.R


@UnstableApi
class SimpleMediaNotificationAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?
) : PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player): CharSequence =
       player.mediaMetadata.albumTitle ?: ""

    // player.mediaMetadata.displayTitle ?: ""

    // "DDDDD"

    override fun createCurrentContentIntent(player: Player): PendingIntent? =
        pendingIntent

    override fun getCurrentContentText(player: Player): CharSequence =
        //  player.mediaMetadata.displayTitle ?: ""
          player.mediaMetadata.title ?: ""


    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {

     val imageLoader = Coil.imageLoader(context) // or create your own instance
        var bitmap : Bitmap? = null
        val request = ImageRequest.Builder(context)
            .data(player.mediaMetadata.artworkUri)
            .target { drawable ->
                // Handle the result.
                 bitmap = (drawable as BitmapDrawable).bitmap
                if(bitmap!=null)
                callback.onBitmap(bitmap!!)
            }
            .build()
        imageLoader.enqueue(request)


        return if(bitmap!=null)
            bitmap
        else
                (ContextCompat.getDrawable(context, R.drawable.radioerror) as BitmapDrawable).bitmap

    }

}
