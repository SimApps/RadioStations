package com.asmtunis.player_service.service.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.ui.PlayerNotificationManager;
import com.asmtunis.player_service.R;
import dagger.hilt.android.qualifiers.ApplicationContext;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 8, 0}, k = 2, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"NOTIFICATION_CHANNEL_ID", "", "NOTIFICATION_CHANNEL_NAME", "NOTIFICATION_ID", "", "player-service_debug"})
public final class SimpleMediaNotificationManagerKt {
    private static final int NOTIFICATION_ID = 909090909;
    private static final java.lang.String NOTIFICATION_CHANNEL_NAME = "Radio FM AM";
    private static final java.lang.String NOTIFICATION_CHANNEL_ID = "Radio FM AM Player Id";
}