package com.amirami.simapp.radiostations.di

import com.amirami.simapp.radiostations.repository.RadioRoomBaseRepository
import com.amirami.simapp.radiostations.repository.RepositoryRadiotRoom
import com.amirami.simapp.radiostations.room.RadioDAO
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
    fun provideShopingListRepository(provideRadioDAO : RadioDAO) : RadioRoomBaseRepository {
        return RepositoryRadiotRoom(provideRadioDAO)
    }
}