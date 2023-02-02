package com.amirami.simapp.radiostations.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amirami.simapp.radiostations.model.RadioEntity
import kotlinx.coroutines.flow.Flow

// ktlint-disable no-wildcard-imports

@Dao
interface RadioDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(radioEntity: RadioEntity): Long

    // @Delete
    // fun delete(customEntity: CustomEntity)

    //  @Query("DELETE FROM custom_table WHERE Id = :id")
    // fun delete(id : Int?)

    @Query("DELETE FROM radio_table WHERE stationuuid = :radiouid and fav=:fav")
    fun deleteFav(radiouid: String?, fav: Boolean)

    @Query("DELETE FROM radio_table WHERE stationuuid = :radiouid and isAlarm=:isalarm")
    fun deleteAlarm(radiouid: String?, isalarm: Boolean)

    @Query("DELETE FROM radio_table WHERE stationuuid IN (SELECT stationuuid FROM radio_table ORDER BY radioid DESC LIMIT 1 OFFSET 8) and fav=:fav")
    fun deletelistened(fav: Boolean)
    //  @Query("UPDATE radio_table SET quantity=:quantity WHERE radiouid = :radiouid")
//    fun updateQuantity(quantity: Double?, id: Long?)

    @Query("DELETE FROM radio_table")
    fun deleteAll()


    @Query("DELETE FROM radio_table  WHERE isAlarm=:isalarm")
    fun deleteAllAlarm(isalarm: Boolean)

    @Query("DELETE FROM radio_table  WHERE isAlarm=:fav")
    fun deleteAllFav(fav: Boolean)

    // @Query("SELECT * FROM custom_table")
    // @Query("SELECT * FROM radio_table WHERE fav=:fav ORDER BY radioname ASC")
    @Query("SELECT * FROM radio_table WHERE fav=:fav ORDER BY radioid DESC")
    fun getAll(fav: Boolean): Flow<List<RadioEntity>>


    @Query("SELECT * FROM radio_table WHERE isAlarm=:isalarm")
    fun getAlarm(isalarm: Boolean): Flow<List<RadioEntity>>
}
