package com.asmtunis.player_service.service;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\u0014\u0010\u0012\u001a\u00020\u000f2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00110\u0014J\u0010\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J\u0010\u0010\u0018\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u001aH\u0017J\u0019\u0010\u001b\u001a\u00020\u000f2\u0006\u0010\u001c\u001a\u00020\u001dH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001eJ\u0011\u0010\u001f\u001a\u00020\u000fH\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010 J\b\u0010!\u001a\u00020\u000fH\u0002R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\""}, d2 = {"Lcom/asmtunis/player_service/service/SimpleMediaServiceHandler;", "Landroidx/media3/common/Player$Listener;", "player", "Landroidx/media3/exoplayer/ExoPlayer;", "(Landroidx/media3/exoplayer/ExoPlayer;)V", "_simpleMediaState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/asmtunis/player_service/service/SimpleMediaState;", "job", "Lkotlinx/coroutines/Job;", "simpleMediaState", "Lkotlinx/coroutines/flow/StateFlow;", "getSimpleMediaState", "()Lkotlinx/coroutines/flow/StateFlow;", "addMediaItem", "", "mediaItem", "Landroidx/media3/common/MediaItem;", "addMediaItemList", "mediaItemList", "", "onIsPlayingChanged", "isPlaying", "", "onPlaybackStateChanged", "playbackState", "", "onPlayerEvent", "playerEvent", "Lcom/asmtunis/player_service/service/PlayerEvent;", "(Lcom/asmtunis/player_service/service/PlayerEvent;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "startProgressUpdate", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "stopProgressUpdate", "player-service_debug"})
public final class SimpleMediaServiceHandler implements androidx.media3.common.Player.Listener {
    private final androidx.media3.exoplayer.ExoPlayer player = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<com.asmtunis.player_service.service.SimpleMediaState> _simpleMediaState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.asmtunis.player_service.service.SimpleMediaState> simpleMediaState = null;
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
    
    private final java.lang.Object startProgressUpdate(kotlin.coroutines.Continuation<? super kotlin.Unit> continuation) {
        return null;
    }
    
    private final void stopProgressUpdate() {
    }
}