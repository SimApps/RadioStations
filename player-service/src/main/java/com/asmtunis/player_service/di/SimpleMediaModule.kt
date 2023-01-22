package com.asmtunis.player_service.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import com.asmtunis.player_service.service.SimpleMediaServiceHandler
import com.asmtunis.player_service.service.notification.SimpleMediaNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SimpleMediaModule {

    @Provides
    @Singleton
    fun provideAudioAttributes(): AudioAttributes =
        AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .setUsage(C.USAGE_MEDIA)
            .build()

    @Provides
    @Singleton
    @UnstableApi
    fun providePlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer =
        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setTrackSelector(DefaultTrackSelector(context))
            .build()

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        player: ExoPlayer
    ): SimpleMediaNotificationManager =
        SimpleMediaNotificationManager(
            context = context,
            player = player
        )

    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer
    ): MediaSession =
        MediaSession.Builder(context, player).build()

    @Provides
    @Singleton
    fun provideServiceHandler(
        player: ExoPlayer
    ): SimpleMediaServiceHandler =
        SimpleMediaServiceHandler(
            player = player
        )
}