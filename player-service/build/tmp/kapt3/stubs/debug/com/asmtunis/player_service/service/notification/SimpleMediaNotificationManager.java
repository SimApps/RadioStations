package com.asmtunis.player_service.service.notification;

import java.lang.System;

@androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.O)
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0003J\b\u0010\r\u001a\u00020\nH\u0003J\u0010\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u0010H\u0003J\u0018\u0010\u0011\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u000b\u001a\u00020\fH\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/asmtunis/player_service/service/notification/SimpleMediaNotificationManager;", "", "context", "Landroid/content/Context;", "player", "Landroidx/media3/exoplayer/ExoPlayer;", "(Landroid/content/Context;Landroidx/media3/exoplayer/ExoPlayer;)V", "notificationManager", "Landroidx/core/app/NotificationManagerCompat;", "buildNotification", "", "mediaSession", "Landroidx/media3/session/MediaSession;", "createNotificationChannel", "startForegroundNotification", "mediaSessionService", "Landroidx/media3/session/MediaSessionService;", "startNotificationService", "player-service_debug"})
public final class SimpleMediaNotificationManager {
    private final android.content.Context context = null;
    private final androidx.media3.exoplayer.ExoPlayer player = null;
    private androidx.core.app.NotificationManagerCompat notificationManager;
    
    @javax.inject.Inject()
    public SimpleMediaNotificationManager(@org.jetbrains.annotations.NotNull()
    @dagger.hilt.android.qualifiers.ApplicationContext()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    androidx.media3.exoplayer.ExoPlayer player) {
        super();
    }
    
    @androidx.media3.common.util.UnstableApi()
    public final void startNotificationService(@org.jetbrains.annotations.NotNull()
    androidx.media3.session.MediaSessionService mediaSessionService, @org.jetbrains.annotations.NotNull()
    androidx.media3.session.MediaSession mediaSession) {
    }
    
    @androidx.media3.common.util.UnstableApi()
    private final void buildNotification(androidx.media3.session.MediaSession mediaSession) {
    }
    
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.O)
    private final void startForegroundNotification(androidx.media3.session.MediaSessionService mediaSessionService) {
    }
    
    @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.O)
    private final void createNotificationChannel() {
    }
}