package com.amirami.simapp.radiostations.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirami.simapp.radiostations.model.RadioEntity
import com.amirami.simapp.radiostations.repository.RadioRoomBaseRepository
import com.amirami.simapp.radiostations.utils.Coroutines
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioRoomViewModel @Inject constructor(
    private val favListRoomBaseRepository: RadioRoomBaseRepository
) : ViewModel() {

    private val tasksEventChannel = Channel<RoomListEvents>()
    val shopListEvents = tasksEventChannel.receiveAsFlow()

    private val liveList: MutableLiveData<MutableList<RadioEntity>> by lazy(LazyThreadSafetyMode.NONE, initializer = {
        MutableLiveData<MutableList<RadioEntity>>()
    })
    private val liveUpdate: MutableLiveData<RadioEntity> by lazy(LazyThreadSafetyMode.NONE, initializer = {
        MutableLiveData<RadioEntity>()
    })

    fun getInstance(): String {
        return this.toString()
    }

    private val _favList  = MutableStateFlow(emptyList<RadioEntity>())
    val favList = _favList.asStateFlow()


    private val _alarmList  = MutableStateFlow(emptyList<RadioEntity>())
    val alarmList = _alarmList.asStateFlow()

    private val _radioList  = MutableStateFlow(emptyList<RadioEntity>())
    val radioList = _radioList.asStateFlow()


    private val _isFav = MutableStateFlow(false)
    val isFav = _isFav.asStateFlow()


    fun getRepositoryInstance(): String {
        return favListRoomBaseRepository.giveRepository()
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
    init {
    getFavRadioRoom()
    getRadioList()
    getAllAlarm()
    }

    fun upsertRadio(item: RadioEntity, msg: String) {
        Coroutines.io(this@RadioRoomViewModel) {
            if (item.name != "") { // prevent add alarm played station
                favListRoomBaseRepository.upsert(item)
                tasksEventChannel.send(RoomListEvents.ProdAddToRoomMsg(msg))
            }
        }
    }

    /*fun updateQuantity(quantity : Double,id:Long) {
        Coroutines.io(this@RadioRoomViewModel) {
            sholistRoomBaseRepository.updateQuantity(quantity,id)
        }
    }*/

    fun delete(raduiuid: String?, fav: Boolean, msg: String) {
        Coroutines.io(this@RadioRoomViewModel) {
            favListRoomBaseRepository.delete(raduiuid, fav)
            tasksEventChannel.send(RoomListEvents.ProdDeleteFromRoomMsg(msg))
        }
    }

    fun updateFav(raduiuid: String?, fav: Boolean, msg: String) {
        Coroutines.io(this@RadioRoomViewModel) {
            favListRoomBaseRepository.updateFav(raduiuid, fav)
            tasksEventChannel.send(RoomListEvents.ProdDeleteFromRoomMsg(msg))
        }
    }



    fun deletelistened(fav: Boolean, msg: String) {
        Coroutines.io(this@RadioRoomViewModel) {
            favListRoomBaseRepository.deletelistened()
            tasksEventChannel.send(RoomListEvents.ProdDeleteFromRoomMsg(msg))
        }
    }

    fun deleteAll(msg: String) {
        Coroutines.io(this@RadioRoomViewModel) {
            favListRoomBaseRepository.deleteAll()
            tasksEventChannel.send(RoomListEvents.ProdDeleteFromRoomMsg(msg))
        }
    }

    fun getRadioList() { // return  liveList
        viewModelScope.launch {
            favListRoomBaseRepository.getAllRadioList().collect { list ->
                _radioList.value = list

            }
        }

    }





  ///// ALARM







    fun upsertRadioAlarm(item: RadioEntity, msg: String) {
        Coroutines.io(this) {
            if (item.name != "") { // prevent add alarm played station
                favListRoomBaseRepository.upsert(item)
                tasksEventChannel.send(RoomListEvents.ProdAddToRoomMsg(msg))
            }
        }
    }

    /*fun updateQuantity(quantity : Double,id:Long) {
        Coroutines.io(this@RadioRoomViewModel) {
            sholistRoomBaseRepository.updateQuantity(quantity,id)
        }
    }*/

    fun deleteAlarm(raduiuid: String?, msg: String) {
        Coroutines.io(this) {
            favListRoomBaseRepository.delete(raduiuid,true)
            tasksEventChannel.send(RoomListEvents.ProdDeleteFromRoomMsg(msg))
        }
    }

    fun deleteAllAlarm(msg: String) {
        Coroutines.io(this) {
            favListRoomBaseRepository.deleteAllAlarm()
            tasksEventChannel.send(RoomListEvents.ProdDeleteFromRoomMsg(msg))
        }
    }

    fun getAllAlarm() {

        viewModelScope.launch() {
            favListRoomBaseRepository.getAllAlarm().collect {list ->
                _alarmList.value = list

            }
        }


    }


    /**
     * FAV
     */

     fun getFavRadioRoom() {
        viewModelScope.launch() {
            favListRoomBaseRepository.getFavList(true).collect { list ->
                _favList.value = list

            }
        }


    }

    fun setFavRadio(
        radioVar: RadioEntity
    ) {
        Log.d("kkjdns","aaa")
        Log.d("kkjdns","cc cc " + radioVar.stationuuid)
        if (!radioVar.fav && radioVar.stationuuid != "") {
            radioVar.fav = true
            _isFav.value = true
            Log.d("kkjdns","ww")

            upsertRadio(radioVar, "Radio added")
        } else if (radioVar.fav) {
            Log.d("kkjdns","cccc")


            _isFav.value = false
                updateFav(
                    radioVar.stationuuid,
                    false,
                    "Radio Deleted"
                )



        }
    }

     fun SetFavStation(radio: RadioEntity) {
      _isFav.value = radio.fav
    }

    sealed class RoomListEvents {
        data class ProdAddToRoomMsg(val msg: String) : RoomListEvents()
        data class ProdDeleteFromRoomMsg(val msg: String) : RoomListEvents()
    }
}
