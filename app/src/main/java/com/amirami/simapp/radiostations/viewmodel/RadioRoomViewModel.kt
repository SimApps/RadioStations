package com.amirami.simapp.radiostations.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amirami.simapp.radiostations.model.RadioRoom
import com.amirami.simapp.radiostations.repository.RadioRoomBaseRepository
import com.amirami.simapp.radiostations.utils.ConvertRadioClass
import com.amirami.simapp.radiostations.utils.Coroutines
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class RadioRoomViewModel  @Inject constructor(
    private val  sholistRoomBaseRepository : RadioRoomBaseRepository
): ViewModel() {
    private val tasksEventChannel = Channel<ShopListEvents>()
    val shopListEvents = tasksEventChannel.receiveAsFlow()

    private val liveList : MutableLiveData<MutableList<RadioRoom>> by lazy(LazyThreadSafetyMode.NONE, initializer = {
        MutableLiveData<MutableList<RadioRoom>>()
    })
    private val liveUpdate : MutableLiveData<RadioRoom> by lazy(LazyThreadSafetyMode.NONE, initializer = {
        MutableLiveData<RadioRoom>()
    })


    private val _nobillShopingRoom = MutableLiveData<RadioRoom>()
    val nobillShopingRoom: LiveData<RadioRoom>
        get() = _nobillShopingRoom

    private val _billShopingRoom = MutableLiveData<List<RadioRoom>>()
    val billShopingRoom: LiveData<List<RadioRoom>>
        get() = _billShopingRoom

    fun getInstance() : String {
        return this.toString()
    }

    fun getRepositoryInstance() : String {
        return sholistRoomBaseRepository.giveRepository()
    }

    @Deprecated("For Static Data")
    fun setItems() {
        Coroutines.default {
            sholistRoomBaseRepository.deleteAll()
            for (index in 0 until 500) {
                // sholistRoomBaseRepository.insert(ConvertList.toEntity(ProductShopingRoom(index,"name $index")))
            }

        }
    }


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







    fun upsertRadio(item : RadioRoom, msg:String) {
        Coroutines.io(this@RadioRoomViewModel) {
            if(item.name!=""){//prevent add alarm played station
                sholistRoomBaseRepository.upsert(ConvertRadioClass.toEntity(item))
                tasksEventChannel.send(ShopListEvents.ProdAddToShopMsg(msg))
            }

        }
    }



    /*fun updateQuantity(quantity : Double,id:Long) {
        Coroutines.io(this@RadioRoomViewModel) {
            sholistRoomBaseRepository.updateQuantity(quantity,id)
        }
    }*/


    fun delete(raduiuid : String?, fav:Boolean, msg:String) {
        Coroutines.io(this@RadioRoomViewModel) {
            sholistRoomBaseRepository.delete(raduiuid,fav)
            tasksEventChannel.send(ShopListEvents.ProdDeleteShopMsg(msg))
        }
    }
    fun deletelistened(fav:Boolean, msg:String) {
        Coroutines.io(this@RadioRoomViewModel) {
            sholistRoomBaseRepository.deletelistened(fav)
            tasksEventChannel.send(ShopListEvents.ProdDeleteShopMsg(msg))
        }
    }


    fun deleteAll(msg:String) {
        Coroutines.io(this@RadioRoomViewModel) {
            sholistRoomBaseRepository.deleteAll()
            tasksEventChannel.send(ShopListEvents.ProdDeleteShopMsg(msg))

        }
    }

     fun getAll(fav: Boolean) : LiveData<MutableList<RadioRoom>> { //return  liveList
        return ConvertRadioClass.toLiveDataListModel(
            sholistRoomBaseRepository.getAll(fav)
        )
    }

    fun putProductRoomInfoToBottomSheetNoBill(radioRoom: RadioRoom){
        _nobillShopingRoom.value=radioRoom

    }

    fun putProductRoomInfoToBottomSheetBill(radioRoom: List<RadioRoom>){

        _billShopingRoom.value= radioRoom
    }



    sealed class ShopListEvents {
        data class ProdAddToShopMsg(val msg:String) : ShopListEvents()
        data class ProdDeleteShopMsg(val msg:String) : ShopListEvents()
    }



}