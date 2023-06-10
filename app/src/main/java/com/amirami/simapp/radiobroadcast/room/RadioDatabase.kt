package com.amirami.simapp.radiobroadcast.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amirami.simapp.radiobroadcast.model.RadioEntity

@Database(
    entities = [RadioEntity::class],
    version = 3

)
abstract class RadioDatabase : RoomDatabase() {

    abstract fun customDao(): RadioDAO
}
