package com.amirami.simapp.radiostations.alarm.di

import android.content.Context
import androidx.room.Room
import com.amirami.simapp.radiostations.alarm.data.local.AlarmDao
import com.amirami.simapp.radiostations.alarm.data.local.AlarmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesAlarmDatabase(@ApplicationContext context: Context): AlarmDatabase {
        return Room.databaseBuilder(context, AlarmDatabase::class.java, AlarmDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration().build()

    }

    @Singleton
    @Provides
    fun providesAlarmDao(alarmDatabase: AlarmDatabase): AlarmDao {
        return alarmDatabase.alarmDao()
    }

}