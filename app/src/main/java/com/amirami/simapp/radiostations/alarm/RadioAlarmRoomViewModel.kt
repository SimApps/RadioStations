package com.amirami.simapp.radiostations.alarm

import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.firestore.ProductFirestoreRepository
import com.amirami.simapp.radiostations.model.RadioRoom
import com.amirami.simapp.radiostations.repository.RadioRoomBaseRepository
import com.amirami.simapp.radiostations.utils.ConvertRadioAlarmClass
import com.amirami.simapp.radiostations.utils.ConvertRadioClass
import com.amirami.simapp.radiostations.utils.Coroutines
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class RadioAlarmRoomViewModel  @Inject constructor(
    private val  sholistRoomBaseRepository : RadioAlarmRoomBaseRepository
    ): ViewModel() {

    private val tasksEventChannel = Channel<ShopListEvents>()
    val shopListEvents = tasksEventChannel.receiveAsFlow()




    fun getInstance() : String {
        return this.toString()
    }

    fun getRepositoryInstance() : String {
        return sholistRoomBaseRepository.giveRepository()
    }
/*
    @Deprecated("For Static Data")
    fun setItems() {
        Coroutines.default {
            sholistRoomBaseRepository.deleteAll()
            for (index in 0 until 500) {
                // sholistRoomBaseRepository.insert(ConvertList.toEntity(ProductShopingRoom(index,"name $index")))
            }

        }
    }
*/

// NEVER NEVER DONT DELTE AND SEE IF IT POSSIBLE TO USE IT
/*
 fun setUpdate(item : ProductShopingRoom) {
        liveUpdate.value = item
    }

    fun getUpdate() : LiveData<ProductShopingRoom> {
        return liveUpdate
    }
    fun updateItem() {
        liveUpdate.value?.let {
            Coroutines.io(this@ShopListRoomViewModel) {
                sholistRoomBaseRepository.update(
                    ConvertShopList.toEntity(it)
                )
            }
        }
    }
*/







    fun upsertRadioAlarm(item : RadioAlarmRoom, msg:String) {
        Coroutines.io(this@RadioAlarmRoomViewModel) {
            if(item.name!=""){//prevent add alarm played station
                sholistRoomBaseRepository.upsert(ConvertRadioAlarmClass.toEntity(item))
                tasksEventChannel.send(ShopListEvents.ProdAddToShopMsg(msg))

            }

        }
    }



    /*fun updateQuantity(quantity : Double,id:Long) {
        Coroutines.io(this@RadioRoomViewModel) {
            sholistRoomBaseRepository.updateQuantity(quantity,id)
        }
    }*/


    fun delete(raduiuid : String?, msg:String) {
        Coroutines.io(this@RadioAlarmRoomViewModel) {
            sholistRoomBaseRepository.delete(raduiuid)
            tasksEventChannel.send(ShopListEvents.ProdDeleteShopMsg(msg))
        }
    }












    fun deleteAll(msg:String) {
        Coroutines.io(this@RadioAlarmRoomViewModel) {
            sholistRoomBaseRepository.deleteAll()
            tasksEventChannel.send(ShopListEvents.ProdDeleteShopMsg(msg))

        }
    }

     fun getAll() : LiveData<MutableList<RadioAlarmRoom>> { //return  liveList
        return ConvertRadioAlarmClass.toLiveDataListModel(
            sholistRoomBaseRepository.getAll()
        )
    }





    sealed class ShopListEvents {
        data class ProdAddToShopMsg(val msg:String) : ShopListEvents()
        data class ProdDeleteShopMsg(val msg:String) : ShopListEvents()
    }



}

