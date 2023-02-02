package com.amirami.simapp.radiostations.viewmodel

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

    private val _lastListnedList  = MutableStateFlow(emptyList<RadioEntity>())
    val lastListnedList = _lastListnedList.asStateFlow()

    fun getRepositoryInstance(): String {
        return favListRoomBaseRepository.giveRepository()
    }

   // @Deprecated("For Static Data")
    fun setItems() {
        Coroutines.default {
            favListRoomBaseRepository.deleteAll()
            //  for (index in 0 until 500) {
            // sholistRoomBaseRepository.insert(ConvertList.toEntity(ProductShopingRoom(index,"name $index")))
            // }
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
    init {
    getFavRadioRoom()
    getLastListened()
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

    fun deletelistened(fav: Boolean, msg: String) {
        Coroutines.io(this@RadioRoomViewModel) {
            favListRoomBaseRepository.deletelistened(fav)
            tasksEventChannel.send(RoomListEvents.ProdDeleteFromRoomMsg(msg))
        }
    }

    fun deleteAll(msg: String) {
        Coroutines.io(this@RadioRoomViewModel) {
            favListRoomBaseRepository.deleteAll()
            tasksEventChannel.send(RoomListEvents.ProdDeleteFromRoomMsg(msg))
        }
    }

    fun getLastListened() { // return  liveList
        viewModelScope.launch {
            favListRoomBaseRepository.getAll(false).collect {list ->
                _lastListnedList.value = list

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
            favListRoomBaseRepository.getAll(true).collect {list ->
                _favList.value = list

            }
        }


    }

    fun setFavRadio(
        radioVar: RadioEntity,
        isFav : Boolean
    ) {

        val radio = RadioEntity()

        if (!isFav && radioVar.stationuuid != "") {
            radio.name = radioVar.name
            radio.tags = radioVar.tags
            radio.stationuuid = radioVar.stationuuid
            radio.country = radioVar.country
            radio.language = radioVar.language
            radio.bitrate = radioVar.bitrate
            radio.streamurl = radioVar.streamurl
            radio.favicon = radioVar.favicon
            radio.homepage = radioVar.homepage

            val radioroom = RadioEntity(
                stationuuid =    radioVar.stationuuid,
                name =  radioVar.name,
                bitrate =  radioVar.bitrate,
                homepage =  radioVar.homepage,
                favicon =  radioVar.favicon,
                tags = radioVar.tags,
                country =  radioVar.country,
                state =   radioVar.state,
                // var RadiostateDB: String?,
                language = radioVar.language,
                streamurl = radioVar.streamurl,
                fav =    true
            )


            upsertRadio(radioroom, "Radio added")
        } else if (isFav) {
                delete(
                    radioVar.stationuuid,
                    true,
                    "Radio Deleted"
                )



        }
    }

     fun isFavoriteStation(stationuuid: String): Boolean {
        var idIn = false
        if (favList.value.isNotEmpty()) {
            loop@ for (i in 0 until favList.value.size) {
                if (stationuuid == favList.value[i].stationuuid /*&& radioRoom[i].fav */) {
                    idIn = true
                    break@loop
                }
            }
        }
        return idIn
    }

    sealed class RoomListEvents {
        data class ProdAddToRoomMsg(val msg: String) : RoomListEvents()
        data class ProdDeleteFromRoomMsg(val msg: String) : RoomListEvents()
    }
}
