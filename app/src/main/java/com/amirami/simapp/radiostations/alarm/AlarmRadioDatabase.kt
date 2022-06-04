package com.amirami.simapp.radiostations.alarm

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AlarmRadioRoomEntity::class],
    version = 1,

)
abstract class AlarmRadioDatabase : RoomDatabase(){

    abstract fun customDao() : AlarmRadioDAO
}