package com.amirami.simapp.radiostations.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amirami.simapp.radiostations.room.RadioDAO
import com.amirami.simapp.radiostations.room.RadioDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideProdShopListDataBase(application : Application, roomCallback : RoomDatabase.Callback) : RadioDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            RadioDatabase::class.java,
            "radio_database"
        )
            .fallbackToDestructiveMigration()
            .addCallback(roomCallback)
            .build()
    }


    @Provides
    fun provideShopListDAO(radioDatabase : RadioDatabase) : RadioDAO {
        return radioDatabase.customDao()
    }


    @Provides
    fun provideRoomDatabaseCallback() : RoomDatabase.Callback {
        return object : RoomDatabase.Callback() {
        }
    }
}