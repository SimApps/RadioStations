package com.amirami.simapp.radiostations.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RadioDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(radioEntity: RadioEntity): Long

    //@Delete
    //fun delete(customEntity: CustomEntity)

    //  @Query("DELETE FROM custom_table WHERE Id = :id")
    // fun delete(id : Int?)

    @Query("DELETE FROM radio_table WHERE radiouid = :radiouid and fav=:fav")
    fun delete(radiouid : String?, fav:Boolean)

    @Query("DELETE FROM radio_table WHERE radioid IN (SELECT radioid FROM radio_table ORDER BY radioid DESC LIMIT 1 OFFSET 8) and fav=:fav")
    fun deletelistened(fav:Boolean)
  //  @Query("UPDATE radio_table SET quantity=:quantity WHERE radiouid = :radiouid")
//    fun updateQuantity(quantity: Double?, id: Long?)

    @Query("DELETE FROM radio_table")
    fun deleteAll()

    //@Query("SELECT * FROM custom_table")
   // @Query("SELECT * FROM radio_table WHERE fav=:fav ORDER BY radioname ASC")
    @Query("SELECT * FROM radio_table WHERE fav=:fav ORDER BY radioid DESC")
    fun getAll(fav:Boolean) : LiveData<List<RadioEntity>>


}