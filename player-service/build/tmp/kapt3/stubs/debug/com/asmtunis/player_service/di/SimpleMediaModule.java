package com.asmtunis.player_service.di;

import java.lang.System;

@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\u001a\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007J\u001a\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007J\u001a\u0010\r\u001a\u00020\n2\b\b\u0001\u0010\u0007\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\u0004H\u0007J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\t\u001a\u00020\nH\u0007\u00a8\u0006\u0011"}, d2 = {"Lcom/asmtunis/player_service/di/SimpleMediaModule;", "", "()V", "provideAudioAttributes", "Landroidx/media3/common/AudioAttributes;", "provideMediaSession", "Landroidx/media3/session/MediaSession;", "context", "Landroid/content/Context;", "player", "Landroidx/media3/exoplayer/ExoPlayer;", "provideNotificationManager", "Lcom/asmtunis/player_service/service/notification/SimpleMediaNotificationManager;", "providePlayer", "audioAttributes", "provideServiceHandler", "Lcom/asmtunis/player_service/service/SimpleMediaServiceHandler;", "player-service_debug"})
@dagger.Module()
public final class SimpleMediaModule {
    
    public SimpleMediaModule() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    @javax.inject.Singleton()
    @dagger.Provides()
    public final androidx.media3.common.AudioAttributes provideAudioAttributes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @androidx.media3.common.util.UnstableApi()
    @javax.inject.Singleton()
    @dagger.Provides()
    public final androidx.media3.exoplayer.ExoPlayer providePlayer(@org.jetbrains.annotations.NotNull()
    @dagger.hilt.android.qualifiers.ApplicationContext()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    androidx.media3.common.AudioAttributes audioAttributes) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.O)
    @javax.inject.Singleton()
    @dagger.Provides()
    public final com.asmtunis.player_service.service.notification.SimpleMediaNotificationManager provideNotificationManager(@org.jetbrains.annotations.NotNull()
    @dagger.hilt.android.qualifiers.ApplicationContext()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    androidx.media3.exoplayer.ExoPlayer player) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @javax.inject.Singleton()
    @dagger.Provides()
    public final androidx.media3.session.MediaSession provideMediaSession(@org.jetbrains.annotations.NotNull()
    @dagger.hilt.android.qualifiers.ApplicationContext()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    androidx.media3.exoplayer.ExoPlayer player) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @javax.inject.Singleton()
    @dagger.Provides()
    public final com.asmtunis.player_service.service.SimpleMediaServiceHandler provideServiceHandler(@org.jetbrains.annotations.NotNull()
    androidx.media3.exoplayer.ExoPlayer player) {
        return null;
    }
}