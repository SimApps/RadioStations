package com.amirami.simapp.radiostations.alarm

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmRadioDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(radioEntity: AlarmRadioRoomEntity): Long

    // @Delete
    // fun delete(customEntity: CustomEntity)

    //  @Query("DELETE FROM custom_table WHERE Id = :id")
    // fun delete(id : Int?)

    @Query("DELETE FROM radio_alarm_table WHERE radiouid = :radiouid")
    fun delete(radiouid: String?)

    //  @Query("UPDATE radio_table SET quantity=:quantity WHERE radiouid = :radiouid")
//    fun updateQuantity(quantity: Double?, id: Long?)

    @Query("DELETE FROM radio_alarm_table")
    fun deleteAll()

    // @Query("SELECT * FROM custom_table")
    // @Query("SELECT * FROM radio_table WHERE fav=:fav ORDER BY radioname ASC")
    @Query("SELECT * FROM radio_alarm_table ORDER BY radioid DESC")
    fun getAll(): LiveData<List<AlarmRadioRoomEntity>>
}
