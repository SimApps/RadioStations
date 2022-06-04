package com.amirami.simapp.radiostations.alarm

import androidx.lifecycle.LiveData
import com.amirami.simapp.radiostations.room.RadioEntity

interface RadioAlarmRoomBaseRepository {

    fun giveRepository() : String

    suspend fun  upsert(radioEntity : AlarmRadioRoomEntity)



   // suspend fun  updateQuantity(quantity : Double,id:Long)


    //suspend fun  delete(customEntity : CustomEntity)
    suspend fun  delete(radioId : String?)



    suspend fun  deleteAll()

    fun  getAll() : LiveData<List<AlarmRadioRoomEntity>>


}
