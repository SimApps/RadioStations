package com.amirami.simapp.radiostations.repository

import androidx.lifecycle.LiveData
import com.amirami.simapp.radiostations.room.RadioEntity

interface RadioRoomBaseRepository {

    fun giveRepository(): String

    suspend fun upsert(radioEntity: RadioEntity)

    // suspend fun  updateQuantity(quantity : Double,id:Long)

    // suspend fun  delete(customEntity : CustomEntity)
    suspend fun delete(radioId: String?, fav: Boolean)

    suspend fun deletelistened(fav: Boolean)

    suspend fun deleteAll()

    fun getAll(fav: Boolean): LiveData<List<RadioEntity>>
}
