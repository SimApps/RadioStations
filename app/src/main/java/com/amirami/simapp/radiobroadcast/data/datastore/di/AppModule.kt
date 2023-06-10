package com.amirami.simapp.radiobroadcast.data.datastore.di

import android.content.Context
import com.amirami.simapp.radiobroadcast.data.datastore.preferences.abstraction.DataStoreRepository
import com.amirami.simapp.radiobroadcast.data.datastore.preferences.implementation.DataStoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDataStoreRepository(
        @ApplicationContext app: Context
    ): DataStoreRepository = DataStoreRepositoryImpl(app)

}