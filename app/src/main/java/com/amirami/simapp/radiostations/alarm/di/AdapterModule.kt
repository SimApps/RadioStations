package com.amirami.simapp.radiostations.alarm.di

import com.amirami.simapp.radiostations.alarm.adapters.AlarmRcAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@InstallIn(ActivityComponent::class)
@Module
class AdapterModule {


    @Provides
    fun providesAlarmRcAdapter(): AlarmRcAdapter {
        return AlarmRcAdapter()
    }


}