package com.amirami.simapp.radiobroadcast.di

import com.amirami.simapp.radiobroadcast.repository.RadioRoomBaseRepository
import com.amirami.simapp.radiobroadcast.repository.RepositoryRadiotRoom
import com.amirami.simapp.radiobroadcast.room.RadioDAO
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
