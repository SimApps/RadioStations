package com.asmtunis.player_service.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import kotlinx.coroutines.*;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0005\u0003\u0004\u0005\u0006\u0007B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0005\b\t\n\u000b\f\u00a8\u0006\r"}, d2 = {"Lcom/asmtunis/player_service/service/PlayerEvent;", "", "()V", "Backward", "Forward", "PlayPause", "Stop", "UpdateProgress", "Lcom/asmtunis/player_service/service/PlayerEvent$Backward;", "Lcom/asmtunis/player_service/service/PlayerEvent$Forward;", "Lcom/asmtunis/player_service/service/PlayerEvent$PlayPause;", "Lcom/asmtunis/player_service/service/PlayerEvent$Stop;", "Lcom/asmtunis/player_service/service/PlayerEvent$UpdateProgress;", "player-service_debug"})
public abstract class PlayerEvent {
    
    private PlayerEvent() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/asmtunis/player_service/service/PlayerEvent$PlayPause;", "Lcom/asmtunis/player_service/service/PlayerEvent;", "()V", "player-service_debug"})
    public static final class PlayPause extends com.asmtunis.player_service.service.PlayerEvent {
        @org.jetbrains.annotations.NotNull()
        public static final com.asmtunis.player_service.service.PlayerEvent.PlayPause INSTANCE = null;
        
        private PlayPause() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/asmtunis/player_service/service/PlayerEvent$Backward;", "Lcom/asmtunis/player_service/service/PlayerEvent;", "()V", "player-service_debug"})
    public static final class Backward extends com.asmtunis.player_service.service.PlayerEvent {
        @org.jetbrains.annotations.NotNull()
        public static final com.asmtunis.player_service.service.PlayerEvent.Backward INSTANCE = null;
        
        private Backward() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/asmtunis/player_service/service/PlayerEvent$Forward;", "Lcom/asmtunis/player_service/service/PlayerEvent;", "()V", "player-service_debug"})
    public static final class Forward extends com.asmtunis.player_service.service.PlayerEvent {
        @org.jetbrains.annotations.NotNull()
        public static final com.asmtunis.player_service.service.PlayerEvent.Forward INSTANCE = null;
        
        private Forward() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/asmtunis/player_service/service/PlayerEvent$Stop;", "Lcom/asmtunis/player_service/service/PlayerEvent;", "()V", "player-service_debug"})
    public static final class Stop extends com.asmtunis.player_service.service.PlayerEvent {
        @org.jetbrains.annotations.NotNull()
        public static final com.asmtunis.player_service.service.PlayerEvent.Stop INSTANCE = null;
        
        private Stop() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/asmtunis/player_service/service/PlayerEvent$UpdateProgress;", "Lcom/asmtunis/player_service/service/PlayerEvent;", "newProgress", "", "(F)V", "getNewProgress", "()F", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "player-service_debug"})
    public static final class UpdateProgress extends com.asmtunis.player_service.service.PlayerEvent {
        private final float newProgress = 0.0F;
        
        @org.jetbrains.annotations.NotNull()
        public final com.asmtunis.player_service.service.PlayerEvent.UpdateProgress copy(float newProgress) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String toString() {
            return null;
        }
        
        public UpdateProgress(float newProgress) {
            super();
        }
        
        public final float component1() {
            return 0.0F;
        }
        
        public final float getNewProgress() {
            return 0.0F;
        }
    }
}