package com.amirami.simapp.radiobroadcastpro.repository

import com.amirami.simapp.radiobroadcastpro.model.RadioEntity
import kotlinx.coroutines.flow.Flow

interface RadioRoomBaseRepository {

    fun giveRepository(): String

    suspend fun upsert(radioEntity: RadioEntity)

    // suspend fun  updateQuantity(quantity : Double,id:Long)

    // suspend fun  delete(customEntity : CustomEntity)
    suspend fun delete(radioId: String?)

    suspend fun deleteAlarm(radioId: String?, isAlarm: Boolean)

    suspend fun deletelistened()

    suspend fun deleteAll()
    suspend fun deleteAllAlarm()
    suspend fun deleteAllFav()

    fun getFavList(fav: Boolean): Flow<List<RadioEntity>>
    fun getLastListenedList(lastListen: Boolean): Flow<List<RadioEntity>>
    fun getAllRadioList(): Flow<List<RadioEntity>>
    fun getAllAlarm(): Flow<List<RadioEntity>>
}