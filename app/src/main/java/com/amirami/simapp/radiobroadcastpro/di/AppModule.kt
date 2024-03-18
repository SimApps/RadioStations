package com.amirami.simapp.radiobroadcastpro.di

import com.amirami.simapp.radiobroadcastpro.repository.RadioRoomBaseRepository
import com.amirami.simapp.radiobroadcastpro.repository.RepositoryRadiotRoom
import com.amirami.simapp.radiobroadcastpro.room.RadioDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRadioListRepository(provideRadioDAO: RadioDAO): RadioRoomBaseRepository {
        return RepositoryRadiotRoom(provideRadioDAO)
    }


}
