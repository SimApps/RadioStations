package com.amirami.simapp.radiobroadcast.pairalarm.di

import android.content.Context
import androidx.room.Room
import com.amirami.simapp.radiobroadcast.pairalarm.database.AppDatabase
import com.amirami.simapp.radiobroadcast.pairalarm.database.dao.AlarmDAO
import com.amirami.simapp.radiobroadcast.pairalarm.repository.AlarmRepository
import com.amirami.simapp.radiobroadcast.pairalarm.util.ALARM_DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, ALARM_DB_NAME).build()
    }

    @Provides
    @Singleton
    fun provideAlarmDao(appDatabase: AppDatabase): AlarmDAO {
        return appDatabase.alarmDao()
    }

    @Provides
    @Singleton
    fun provideAlarmRepository(alarmDAO: AlarmDAO): AlarmRepository {
        return AlarmRepository(alarmDAO)
    }
}
