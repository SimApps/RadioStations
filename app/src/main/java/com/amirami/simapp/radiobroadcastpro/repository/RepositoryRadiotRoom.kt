package com.amirami.simapp.radiobroadcastpro.repository

import com.amirami.simapp.radiobroadcastpro.room.RadioDAO
import com.amirami.simapp.radiobroadcastpro.model.RadioEntity
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

    override suspend fun delete(radiouId: String?)
      =  radioDAO.deleteFav(radiouId)


    override suspend fun deleteAlarm(radioId: String?, isAlarm: Boolean)
    = radioDAO.deleteAlarm(radioId, true)

    override suspend fun deletelistened()
        =radioDAO.deletelistened()


    override suspend fun deleteAll()
       = radioDAO.deleteAll()

    override suspend fun deleteAllAlarm()  =radioDAO.deleteAllAlarm(true)

    override suspend fun deleteAllFav()  =radioDAO.deleteAllFav(true)


    override fun getFavList(fav: Boolean): Flow<List<RadioEntity>> {
        return radioDAO.getFavList(fav)
    }

    override fun getLastListenedList(lastListen: Boolean): Flow<List<RadioEntity>> = radioDAO.getLastListenedList(lastListen)
    override fun getAllRadioList(): Flow<List<RadioEntity>> = radioDAO.getAllRadioList()


    override fun getAllAlarm(): Flow<List<RadioEntity>> =
        radioDAO.getAlarm(true)

    //endregion
}
