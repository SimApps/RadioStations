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

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015J\u0014\u0010\u0016\u001a\u00020\u00132\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00150\u0018J\u0010\u0010\u0019\u001a\u00020\u00132\u0006\u0010\u001a\u001a\u00020\u001bH\u0016J\u0010\u0010\u001c\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J\u0010\u0010\u001f\u001a\u00020\u00132\u0006\u0010 \u001a\u00020!H\u0016J\u0010\u0010\"\u001a\u00020\u00132\u0006\u0010#\u001a\u00020$H\u0017J\u0010\u0010%\u001a\u00020\u00132\u0006\u0010&\u001a\u00020\'H\u0016J\u0019\u0010(\u001a\u00020\u00132\u0006\u0010)\u001a\u00020*H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010+J\u0010\u0010,\u001a\u00020\u00132\u0006\u0010-\u001a\u00020$H\u0016J\u0010\u0010.\u001a\u00020\u00132\u0006\u0010/\u001a\u00020\u001bH\u0016J\u0011\u00100\u001a\u00020\u0013H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u00101J\b\u00102\u001a\u00020\u0013H\u0002R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\r\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u00063"}, d2 = {"Lcom/asmtunis/player_service/service/SimpleMediaServiceHandler;", "Landroidx/media3/common/Player$Listener;", "player", "Landroidx/media3/exoplayer/ExoPlayer;", "(Landroidx/media3/exoplayer/ExoPlayer;)V", "_icyState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_simpleMediaState", "Lcom/asmtunis/player_service/service/SimpleMediaState;", "icyState", "Lkotlinx/coroutines/flow/StateFlow;", "getIcyState", "()Lkotlinx/coroutines/flow/StateFlow;", "job", "Lkotlinx/coroutines/Job;", "simpleMediaState", "getSimpleMediaState", "addMediaItem", "", "mediaItem", "Landroidx/media3/common/MediaItem;", "addMediaItemList", "mediaItemList", "", "onIsPlayingChanged", "isPlaying", "", "onMediaMetadataChanged", "mediaMetadata", "Landroidx/media3/common/MediaMetadata;", "onPlaybackParametersChanged", "playbackParameters", "Landroidx/media3/common/PlaybackParameters;", "onPlaybackStateChanged", "playbackState", "", "onPlayerError", "error", "Landroidx/media3/common/PlaybackException;", "onPlayerEvent", "playerEvent", "Lcom/asmtunis/player_service/service/PlayerEvent;", "(Lcom/asmtunis/player_service/service/PlayerEvent;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "onRepeatModeChanged", "repeatMode", "onShuffleModeEnabledChanged", "shuffleModeEnabled", "startProgressUpdate", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "stopProgressUpdate", "player-service_debug"})
public final class SimpleMediaServiceHandler implements androidx.media3.common.Player.Listener {
    private final androidx.media3.exoplayer.ExoPlayer player = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<com.asmtunis.player_service.service.SimpleMediaState> _simpleMediaState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.asmtunis.player_service.service.SimpleMediaState> simpleMediaState = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _icyState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> icyState = null;
    private kotlinx.coroutines.Job job;
    
    @javax.inject.Inject()
    public SimpleMediaServiceHandler(@org.jetbrains.annotations.NotNull()
    androidx.media3.exoplayer.ExoPlayer player) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.asmtunis.player_service.service.SimpleMediaState> getSimpleMediaState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getIcyState() {
        return null;
    }
    
    public final void addMediaItem(@org.jetbrains.annotations.NotNull()
    androidx.media3.common.MediaItem mediaItem) {
    }
    
    public final void addMediaItemList(@org.jetbrains.annotations.NotNull()
    java.util.List<androidx.media3.common.MediaItem> mediaItemList) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object onPlayerEvent(@org.jetbrains.annotations.NotNull()
    com.asmtunis.player_service.service.PlayerEvent playerEvent, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation) {
        return null;
    }
    
    @android.annotation.SuppressLint(value = {"SwitchIntDef"})
    @java.lang.Override()
    public void onPlaybackStateChanged(int playbackState) {
    }
    
    @kotlin.OptIn(markerClass = {kotlinx.coroutines.DelicateCoroutinesApi.class})
    @java.lang.Override()
    public void onIsPlayingChanged(boolean isPlaying) {
    }
    
    @java.lang.Override()
    public void onMediaMetadataChanged(@org.jetbrains.annotations.NotNull()
    androidx.media3.common.MediaMetadata mediaMetadata) {
    }
    
    @java.lang.Override()
    public void onPlayerError(@org.jetbrains.annotations.NotNull()
    androidx.media3.common.PlaybackException error) {
    }
    
    @java.lang.Override()
    public void onPlaybackParametersChanged(@org.jetbrains.annotations.NotNull()
    androidx.media3.common.PlaybackParameters playbackParameters) {
    }
    
    @java.lang.Override()
    public void onRepeatModeChanged(int repeatMode) {
    }
    
    @java.lang.Override()
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }
    
    private final java.lang.Object startProgressUpdate(kotlin.coroutines.Continuation<? super kotlin.Unit> continuation) {
        return null;
    }
    
    private final void stopProgressUpdate() {
    }
}