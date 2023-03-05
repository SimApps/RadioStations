package com.asmtunis.alarm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asmtunis.alarm.data.model.Alarm

@Database(entities = [Alarm::class], version = 1, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        val DATABASE_NAME = "ALARM_DATABASE"
    }


}