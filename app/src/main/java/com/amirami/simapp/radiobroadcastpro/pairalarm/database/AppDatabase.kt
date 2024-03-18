package com.amirami.simapp.radiobroadcastpro.pairalarm.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amirami.simapp.radiobroadcastpro.pairalarm.database.dao.AlarmDAO
import com.amirami.simapp.radiobroadcastpro.pairalarm.database.table.AlarmData
import com.amirami.simapp.radiobroadcastpro.pairalarm.database.table.RadioConverter

@Database(entities = [AlarmData::class], version = 2, exportSchema = false)

@TypeConverters(RadioConverter::class)

abstract class AppDatabase: RoomDatabase() {
    abstract fun alarmDao(): AlarmDAO
}