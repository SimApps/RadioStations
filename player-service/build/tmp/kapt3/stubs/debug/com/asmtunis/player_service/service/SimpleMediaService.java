package com.asmtunis.player_service.service;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0015\u001a\u00020\u0016H\u0016J\u0010\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\"\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\u0006\u0010\u001e\u001a\u00020\u001b2\u0006\u0010\u001f\u001a\u00020\u001bH\u0017R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001e\u0010\t\u001a\u00020\n8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001e\u0010\u000f\u001a\u00020\u00108\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014\u00a8\u0006 "}, d2 = {"Lcom/asmtunis/player_service/service/SimpleMediaService;", "Landroidx/media3/session/MediaSessionService;", "()V", "mediaSession", "Landroidx/media3/session/MediaSession;", "getMediaSession", "()Landroidx/media3/session/MediaSession;", "setMediaSession", "(Landroidx/media3/session/MediaSession;)V", "notificationManager", "Lcom/asmtunis/player_service/service/notification/SimpleMediaNotificationManager;", "getNotificationManager", "()Lcom/asmtunis/player_service/service/notification/SimpleMediaNotificationManager;", "setNotificationManager", "(Lcom/asmtunis/player_service/service/notification/SimpleMediaNotificationManager;)V", "player", "Landroidx/media3/exoplayer/ExoPlayer;", "getPlayer", "()Landroidx/media3/exoplayer/ExoPlayer;", "setPlayer", "(Landroidx/media3/exoplayer/ExoPlayer;)V", "onDestroy", "", "onGetSession", "controllerInfo", "Landroidx/media3/session/MediaSession$ControllerInfo;", "onStartCommand", "", "intent", "Landroid/content/Intent;", "flags", "startId", "player-service_debug"})
@dagger.hilt.android.AndroidEntryPoint()
public final class SimpleMediaService extends androidx.media3.session.MediaSessionService {
    @javax.inject.Inject()
    public androidx.media3.exoplayer.ExoPlayer player;
    @javax.inject.Inject()
    public androidx.media3.session.MediaSession mediaSession;
    @javax.inject.Inject()
    public com.asmtunis.player_service.service.notification.SimpleMediaNotificationManager notificationManager;
    
    public SimpleMediaService() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.media3.exoplayer.ExoPlayer getPlayer() {
        return null;
    }
    
    public final void setPlayer(@org.jetbrains.annotations.NotNull()
    androidx.media3.exoplayer.ExoPlayer p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.media3.session.MediaSession getMediaSession() {
        return null;
    }
    
    public final void setMediaSession(@org.jetbrains.annotations.NotNull()
    androidx.media3.session.MediaSession p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.asmtunis.player_service.service.notification.SimpleMediaNotificationManager getNotificationManager() {
        return null;
    }
    
    public final void setNotificationManager(@org.jetbrains.annotations.NotNull()
    com.asmtunis.player_service.service.notification.SimpleMediaNotificationManager p0) {
    }
    
    @androidx.media3.common.util.UnstableApi()
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.O)
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public androidx.media3.session.MediaSession onGetSession(@org.jetbrains.annotations.NotNull()
    androidx.media3.session.MediaSession.ControllerInfo controllerInfo) {
        return null;
    }
}