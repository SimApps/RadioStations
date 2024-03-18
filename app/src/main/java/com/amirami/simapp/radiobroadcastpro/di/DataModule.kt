package com.amirami.simapp.radiobroadcastpro.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amirami.simapp.radiobroadcastpro.room.RadioDAO
import com.amirami.simapp.radiobroadcastpro.room.RadioDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    fun provideRoomDatabaseCallback(): RoomDatabase.Callback {
        return object : RoomDatabase.Callback() {
        }
    }

    @Provides
    @Singleton
    fun provideProdShopListDataBase(application: Application, roomCallback: RoomDatabase.Callback): RadioDatabase {
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
    fun provideRadioDAO(radioDatabase: RadioDatabase): RadioDAO {
        return radioDatabase.customDao()
    }


}
