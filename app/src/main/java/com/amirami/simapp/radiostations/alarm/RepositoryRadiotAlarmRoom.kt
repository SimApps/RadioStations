package com.amirami.simapp.radiostations.alarm

import androidx.lifecycle.LiveData
import javax.inject.Inject

class RepositoryRadiotAlarmRoom  @Inject constructor(
    private val radioDAO : AlarmRadioDAO
): RadioAlarmRoomBaseRepository {



    override fun giveRepository() : String {
        return this.toString()
    }



    //region CRUD Operation


    override suspend fun upsert(radioEntity : AlarmRadioRoomEntity) {
        radioDAO.upsert(radioEntity)
    }

    /*override suspend fun updateQuantity(quantity : Double,id:Long) {
        radioDAO.updateQuantity(
            quantity,id
        )
    }*/


    /*override suspend fun delete(customEntity: CustomEntity) {
        println("${customEntity.name}")
        customDao.delete(
            customEntity.name
        )
    }*/

    override suspend fun delete(idradio: String?) {
        radioDAO.delete(idradio)
    }



    override suspend fun deleteAll() {
        radioDAO.deleteAll()
    }

    override fun getAll() : LiveData<List<AlarmRadioRoomEntity>> {
        return radioDAO.getAll()
    }
    //endregion
}