// Generated by Dagger (https://dagger.dev).
package com.asmtunis.player_service.di;

import android.content.Context;
import androidx.media3.exoplayer.ExoPlayer;
import com.asmtunis.player_service.service.notification.SimpleMediaNotificationManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class SimpleMediaModule_ProvideNotificationManagerFactory implements Factory<SimpleMediaNotificationManager> {
  private final SimpleMediaModule module;

  private final Provider<Context> contextProvider;

  private final Provider<ExoPlayer> playerProvider;

  public SimpleMediaModule_ProvideNotificationManagerFactory(SimpleMediaModule module,
      Provider<Context> contextProvider, Provider<ExoPlayer> playerProvider) {
    this.module = module;
    this.contextProvider = contextProvider;
    this.playerProvider = playerProvider;
  }

  @Override
  public SimpleMediaNotificationManager get() {
    return provideNotificationManager(module, contextProvider.get(), playerProvider.get());
  }

  public static SimpleMediaModule_ProvideNotificationManagerFactory create(SimpleMediaModule module,
      Provider<Context> contextProvider, Provider<ExoPlayer> playerProvider) {
    return new SimpleMediaModule_ProvideNotificationManagerFactory(module, contextProvider, playerProvider);
  }

  public static SimpleMediaNotificationManager provideNotificationManager(
      SimpleMediaModule instance, Context context, ExoPlayer player) {
    return Preconditions.checkNotNullFromProvides(instance.provideNotificationManager(context, player));
  }
}
