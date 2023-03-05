// Generated by Dagger (https://dagger.dev).
package com.asmtunis.player_service.di;

import androidx.media3.exoplayer.ExoPlayer;
import com.asmtunis.player_service.service.SimpleMediaServiceHandler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class SimpleMediaModule_ProvideServiceHandlerFactory implements Factory<SimpleMediaServiceHandler> {
  private final SimpleMediaModule module;

  private final Provider<ExoPlayer> playerProvider;

  public SimpleMediaModule_ProvideServiceHandlerFactory(SimpleMediaModule module,
      Provider<ExoPlayer> playerProvider) {
    this.module = module;
    this.playerProvider = playerProvider;
  }

  @Override
  public SimpleMediaServiceHandler get() {
    return provideServiceHandler(module, playerProvider.get());
  }

  public static SimpleMediaModule_ProvideServiceHandlerFactory create(SimpleMediaModule module,
      Provider<ExoPlayer> playerProvider) {
    return new SimpleMediaModule_ProvideServiceHandlerFactory(module, playerProvider);
  }

  public static SimpleMediaServiceHandler provideServiceHandler(SimpleMediaModule instance,
      ExoPlayer player) {
    return Preconditions.checkNotNullFromProvides(instance.provideServiceHandler(player));
  }
}
