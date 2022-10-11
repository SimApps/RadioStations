package com.amirami.simapp.radiostations.di

import com.amirami.simapp.radiostations.alarm.AlarmRadioDAO
import com.amirami.simapp.radiostations.alarm.RadioAlarmRoomBaseRepository
import com.amirami.simapp.radiostations.alarm.RepositoryRadiotAlarmRoom
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
    fun provideRadioListRepository(provideRadioDAO: RadioDAO): RadioRoomBaseRepository {
        return RepositoryRadiotRoom(provideRadioDAO)
    }

    @Provides
    @Singleton
    fun provideRadioAlarmListRepository(provideRadioDAO: AlarmRadioDAO): RadioAlarmRoomBaseRepository {
        return RepositoryRadiotAlarmRoom(provideRadioDAO)
    }
}
