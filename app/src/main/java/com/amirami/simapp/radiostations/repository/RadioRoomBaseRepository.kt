package com.amirami.simapp.radiostations.repository

import androidx.lifecycle.LiveData
import com.amirami.simapp.radiostations.model.RadioEntity
import kotlinx.coroutines.flow.Flow

interface RadioRoomBaseRepository {

    fun giveRepository(): String

    suspend fun upsert(radioEntity: RadioEntity)

    // suspend fun  updateQuantity(quantity : Double,id:Long)

    // suspend fun  delete(customEntity : CustomEntity)
    suspend fun delete(radioId: String?, fav: Boolean)
    suspend fun deleteAlarm(radioId: String?, isAlarm: Boolean)

    suspend fun deletelistened(fav: Boolean)

    suspend fun deleteAll()
    suspend fun deleteAllAlarm()
    suspend fun deleteAllFav()

    fun getAll(fav: Boolean): Flow<List<RadioEntity>>
    fun getAllAlarm(): Flow<List<RadioEntity>>
}
