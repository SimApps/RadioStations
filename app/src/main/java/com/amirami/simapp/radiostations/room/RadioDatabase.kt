package com.amirami.simapp.radiostations.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RadioEntity::class],
    version = 2,

)
abstract class RadioDatabase : RoomDatabase(){

    abstract fun customDao() : RadioDAO
}