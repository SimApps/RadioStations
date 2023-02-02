package com.asmtunis.player_service.service.notification;

import java.lang.System;

@androidx.media3.common.util.UnstableApi()
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\r\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006J\u0012\u0010\u0007\u001a\u0004\u0018\u00010\u00052\u0006\u0010\b\u001a\u00020\tH\u0016J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\tH\u0016J\u0010\u0010\f\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\tH\u0016J\u001e\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\b\u001a\u00020\t2\n\u0010\u000f\u001a\u00060\u0010R\u00020\u0011H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/asmtunis/player_service/service/notification/SimpleMediaNotificationAdapter;", "Landroidx/media3/ui/PlayerNotificationManager$MediaDescriptionAdapter;", "context", "Landroid/content/Context;", "pendingIntent", "Landroid/app/PendingIntent;", "(Landroid/content/Context;Landroid/app/PendingIntent;)V", "createCurrentContentIntent", "player", "Landroidx/media3/common/Player;", "getCurrentContentText", "", "getCurrentContentTitle", "getCurrentLargeIcon", "Landroid/graphics/Bitmap;", "callback", "Landroidx/media3/ui/PlayerNotificationManager$BitmapCallback;", "Landroidx/media3/ui/PlayerNotificationManager;", "player-service_debug"})
public final class SimpleMediaNotificationAdapter implements androidx.media3.ui.PlayerNotificationManager.MediaDescriptionAdapter {
    private final android.content.Context context = null;
    private final android.app.PendingIntent pendingIntent = null;
    
    public SimpleMediaNotificationAdapter(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.app.PendingIntent pendingIntent) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.CharSequence getCurrentContentTitle(@org.jetbrains.annotations.NotNull()
    androidx.media3.common.Player player) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public android.app.PendingIntent createCurrentContentIntent(@org.jetbrains.annotations.NotNull()
    androidx.media3.common.Player player) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.CharSequence getCurrentContentText(@org.jetbrains.annotations.NotNull()
    androidx.media3.common.Player player) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public android.graphics.Bitmap getCurrentLargeIcon(@org.jetbrains.annotations.NotNull()
    androidx.media3.common.Player player, @org.jetbrains.annotations.NotNull()
    androidx.media3.ui.PlayerNotificationManager.BitmapCallback callback) {
        return null;
    }
}