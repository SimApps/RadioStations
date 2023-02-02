package com.amirami.simapp.radiostations.repository

import androidx.lifecycle.LiveData
import com.amirami.simapp.radiostations.room.RadioDAO
import com.amirami.simapp.radiostations.model.RadioEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryRadiotRoom @Inject constructor(
    private val radioDAO: RadioDAO
) : RadioRoomBaseRepository {

    override fun giveRepository(): String {
        return this.toString()
    }

    //region CRUD Operation

    override suspend fun upsert(radioEntity: RadioEntity) {
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

    override suspend fun delete(radiouId: String?, fav: Boolean)
      =  radioDAO.deleteFav(radiouId, fav)

    override suspend fun deleteAlarm(radioId: String?, isAlarm: Boolean)
    = radioDAO.deleteAlarm(radioId, true)

    override suspend fun deletelistened(fav: Boolean)
        =radioDAO.deletelistened(fav)


    override suspend fun deleteAll()
       = radioDAO.deleteAll()

    override suspend fun deleteAllAlarm()  =radioDAO.deleteAllAlarm(true)

    override suspend fun deleteAllFav()  =radioDAO.deleteAllFav(true)


    override fun getAll(fav: Boolean): Flow<List<RadioEntity>> {
        return radioDAO.getAll(fav)
    }

    override fun getAllAlarm(): Flow<List<RadioEntity>> =
        radioDAO.getAlarm(true)

    //endregion
}
