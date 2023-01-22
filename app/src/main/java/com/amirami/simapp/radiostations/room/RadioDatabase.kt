package com.amirami.simapp.radiostations.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amirami.simapp.radiostations.model.RadioEntity

@Database(
    entities = [RadioEntity::class],
    version = 2

)
abstract class RadioDatabase : RoomDatabase() {

    abstract fun customDao(): RadioDAO
}
