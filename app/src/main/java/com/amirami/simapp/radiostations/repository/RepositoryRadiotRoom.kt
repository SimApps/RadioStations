package com.amirami.simapp.radiostations.repository

import androidx.lifecycle.LiveData
import com.amirami.simapp.radiostations.room.RadioDAO
import com.amirami.simapp.radiostations.room.RadioEntity
import javax.inject.Inject

class RepositoryRadiotRoom  @Inject constructor(
    private val radioDAO : RadioDAO
): RadioRoomBaseRepository {



    override fun giveRepository() : String {
        return this.toString()
    }



    //region CRUD Operation


    override suspend fun upsert(radioEntity : RadioEntity) {
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

    override suspend fun delete(radiouid: String?, fav:Boolean) {
        radioDAO.delete(radiouid,fav)
    }
    override suspend fun deletelistened(fav:Boolean) {
        radioDAO.deletelistened(fav)
    }


    override suspend fun deleteAll() {
        radioDAO.deleteAll()
    }

    override fun getAll(fav:Boolean) : LiveData<List<RadioEntity>> {
        return radioDAO.getAll(fav)
    }
    //endregion
}