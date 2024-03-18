package com.amirami.simapp.radiobroadcastpro.viewmodel

import android.app.Application
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.amirami.player_service.service.PlayerEvent
import com.amirami.player_service.service.SimpleMediaServiceHandler
import com.amirami.simapp.radiobroadcastpro.DefaultDispatcher
import com.amirami.simapp.radiobroadcastpro.IoDispatcher
import com.amirami.simapp.radiobroadcastpro.MainActivity
import com.amirami.simapp.radiobroadcastpro.RadioFunction
import com.amirami.simapp.radiobroadcastpro.RadioFunction.getPackageInfoCompat
import com.amirami.simapp.radiobroadcastpro.hiltcontainer.RadioApplication
import com.amirami.simapp.radiobroadcastpro.model.RadioEntity
import com.amirami.simapp.radiobroadcastpro.repository.RadioRoomBaseRepository
import com.amirami.simapp.radiobroadcastpro.utils.Coroutines
import com.amirami.simapp.radiobroadcastpro.utils.datamonitor.DataUsageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi @HiltViewModel
class InfoViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle,
    private val simpleMediaServiceHandler: SimpleMediaServiceHandler,
    private val favListRoomBaseRepository: RadioRoomBaseRepository,

    app: Application
) : AndroidViewModel(app) { // : ViewModel() {
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





    fun getRepositoryInstance(): String {
        return favListRoomBaseRepository.giveRepository()
    }

    private val dialogueEventsChannel = MutableSharedFlow<ChooseDefBottomSheetEvents>()
    val dialogueEvents = dialogueEventsChannel.asSharedFlow()

    private val recordEventsChannel = MutableSharedFlow<RecordInfoEvents>()
    val recordEvents = recordEventsChannel.asSharedFlow()

    private val _putRadioInfo = MutableStateFlow(RadioEntity())
    val putRadioInfo = _putRadioInfo.asStateFlow()



    private val _putRadioAlarmInfo = MutableStateFlow(RadioEntity())
    val putRadioAlarmInfo: StateFlow<RadioEntity>
        get() = _putRadioAlarmInfo

    private val _putTitleText = MutableStateFlow("")
    val putTitleText: StateFlow<String>
        get() = _putTitleText



    private val _putSearchQuery = MutableSharedFlow<String>(/*replay = 1*/)
    val putSearchQuery = _putSearchQuery.asSharedFlow()

    private val _putDataConsumption = MutableSharedFlow<String>()
    val putDataConsumption: SharedFlow<String> = _putDataConsumption

    private val _putDataConsumptionTimer = MutableStateFlow(-1251L)
    val putDataConsumptionTimer: StateFlow<kotlin.Long> = _putDataConsumptionTimer

    private var job: Job? = null
    private val _putTimer = MutableStateFlow(-1)
    val putTimer = _putTimer.asStateFlow() // .takeWhile { isValid }




    init {
        putDataConsumption()
    }

    private fun putDataConsumption() {
        viewModelScope.launch {
                val networkStatsManager = getApplication<RadioApplication>().getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
                val managers = getApplication<RadioApplication>().packageManager
                val info: ApplicationInfo = managers.getPackageInfoCompat(getApplication<RadioApplication>().packageName, 0).applicationInfo
                val uid: Int = info.uid
                // val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                // val manager= DataUsageManager(networkStatsManager, Settings.Secure.ANDROID_ID)
                // val manager= DataUsageManager(networkStatsManager, uid.toString())
                val manager = DataUsageManager(networkStatsManager, getApplication<RadioApplication>().packageName)

                if (MainActivity.data == 0L) {
                    MainActivity.data = manager.getUsage(uid).downloads + manager.getUsage(uid).uploads
                }

                while (true) {
                    _putDataConsumption.emit(RadioFunction.bytesIntoHumanReadable(manager.getUsage(uid).downloads + manager.getUsage(uid).uploads - MainActivity.data))
                    delay(10)
                }
        }
    }

    fun putDataConsumptiontimer(data: Long) {
        job = viewModelScope.launch {

                val networkStatsManager = getApplication<RadioApplication>().getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
                val managers = getApplication<RadioApplication>().packageManager
                val info: ApplicationInfo = managers.getPackageInfoCompat(getApplication<RadioApplication>().packageName, 0).applicationInfo

                val uid: Int = info.uid

                val manager = DataUsageManager(networkStatsManager, getApplication<RadioApplication>().packageName)

                if (MainActivity.data == 0L) {
                    MainActivity.data = manager.getUsage(uid).downloads + manager.getUsage(uid).uploads
                }

                MainActivity.initial_data_consumed = manager.getUsage(uid).downloads + manager.getUsage(uid).uploads - MainActivity.data

                while (true) {
                    val final_data_toconsume = MainActivity.initial_data_consumed + data

                    _putDataConsumptionTimer.value = final_data_toconsume - ((manager.getUsage(uid).downloads + manager.getUsage(uid).uploads - MainActivity.data))
                    delay(10)
                }
        }
    }

    fun puttimer(nbrMinute: Int) {
        job?.cancel()
        job = viewModelScope.launch {
            if (nbrMinute <= 0) job?.cancel()
            //   while (on) {
            repeat(nbrMinute) {
                _putTimer.value = nbrMinute - it // emit( nbrMinute - it)
                delay(1000)
            }
            // }
        }
    }

    fun pause() {
        job?.cancel()
    }

    fun stoptimer(stopplayer: Boolean) {
        viewModelScope.launch {
            job?.cancel()
            _putTimer.value = -1 // .emit(-1)
            if (stopplayer) stopPlayer()
        }
    }

    fun stopdatatimer(stopplayer: Boolean) {
        viewModelScope.launch {
            job?.cancel()
            _putDataConsumptionTimer.value = -1L

            if (stopplayer) stopPlayer()
        }
    }




    private fun stopPlayer() {
       // if (Exoplayer.is_downloading) {
        //    MainActivity.downloader?.cancelDownload()
        //    Exoplayer.is_downloading = false // without this line player continue paly and rec stop
       // }
        viewModelScope.launch {
            simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
        }

       // stopService(getApplication<RadioApplication>(), true)
    }

/*
    fun start(times: Int = 30) {
        if (_putTimer.value == 0) _putTimer.value = times
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                if (_putTimer.value <= 0) {
                    job?.cancel()
                    return@launch
                }
                delay(timeMillis = 1000)
                _putTimer.value -= 1
            }
        }
    }

    fun pause() {
        job?.cancel()
    }

    fun stop() {
        job?.cancel()
        _putTimer.value = 0
    }
    */

    fun countDownTimer(startingValue: Int) = flow<Int> {
        viewModelScope.launch {
            var currentValue = startingValue
            emit(startingValue)
            while (currentValue > 0) {
                delay(1000)
                currentValue--
                emit(currentValue)
            }
        }
    }

  /*  val countDownTimer = flow<Int>{
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue>0){
            delay(1000)
            currentValue--
            emit(currentValue)
        }
    }*/

    fun putDefCountryInfo(country: String) {
        viewModelScope.launch {
            dialogueEventsChannel.emit(ChooseDefBottomSheetEvents.PutDefCountryInfo(country))
        }
    }

    fun putDefServerInfo(server: String) {
        viewModelScope.launch {
            dialogueEventsChannel.emit(ChooseDefBottomSheetEvents.PutDefServerInfo(server))
        }
    }

    fun putDefThemeInfo(dark: Boolean, systheme: Boolean) {
        viewModelScope.launch {
            dialogueEventsChannel.emit(ChooseDefBottomSheetEvents.PutDefThemeInfo(dark, systheme))
        }
    }

    fun putLogInInfo(id: String) {
        viewModelScope.launch {
            dialogueEventsChannel.emit(ChooseDefBottomSheetEvents.PutLogInDialogueInfo(id))
        }
    }
    fun putDeleteUsersDialogueInfo(id: String) {
        viewModelScope.launch {
            dialogueEventsChannel.emit(ChooseDefBottomSheetEvents.PutDeleteUsersDialogueInfo(id))
        }
    }

    fun putUpdateRecordInfo(update: Boolean, position: Int) {
        viewModelScope.launch {
            recordEventsChannel.emit(RecordInfoEvents.UpdateRecList(update, position))
        }
    }

    fun putRadioInfo(radiovar: RadioEntity) {
        viewModelScope.launch {
            _putRadioInfo.value = radiovar
        }
    }

    fun isFavRadio (
        radioVar: RadioEntity
    ): Boolean
     =   _favList.value.any { it.stationuuid == radioVar.stationuuid}

    fun setFavRadio(
        radioVar: RadioEntity
    ) {
        val isFav = isFavRadio(radioVar)
        if (!isFav && radioVar.stationuuid != "") {
            upsertRadio(radioVar.copy(fav = true), "Radio added")
        } else if (isFav) {

          upsertRadio(radioVar.copy(fav = false), "Radio added")
            //      delete(raduiuid = radioVar.stationuuid, msg = "deleted")
            /*  updateFav(
                  radioVar.stationuuid,
                  false,
                  "Radio Deleted"
              )*/



        }


    }

    fun putRadioalarmInfo(radiovar: RadioEntity) {
        viewModelScope.launch {
            // radioEventsChannel.send(RadioInfoEvents.PutDefCountryInfo(radiovar))

            _putRadioAlarmInfo.value = radiovar
        }
    }

    fun putTitleText(title: String) {
        viewModelScope.launch {
            _putTitleText.value = title
        }
    }


    fun putSearchquery(query: String) {
        viewModelScope.launch {
            _putSearchQuery.emit(query)
            // _putSearchQuery.value = query
        }
    }

    sealed class ChooseDefBottomSheetEvents {
        data class PutDefCountryInfo(val country: String) : ChooseDefBottomSheetEvents()
        data class PutDefServerInfo(val server: String) : ChooseDefBottomSheetEvents()
        data class PutDefThemeInfo(val dark: Boolean, val systheme: Boolean) : ChooseDefBottomSheetEvents()
        data class PutLogInDialogueInfo(val id: String) : ChooseDefBottomSheetEvents()
        data class PutDeleteUsersDialogueInfo(val id: String) : ChooseDefBottomSheetEvents()
    }

    sealed class RecordInfoEvents {
        data class UpdateRecList(val update: Boolean, val position: Int) : RecordInfoEvents()
    }



    init {
        getFavRadioRoom()
        getRadioList()
        getAllAlarm()
    }

    fun upsertRadio(item: RadioEntity, msg: String) {
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

    fun delete(raduiuid: String?, msg: String) {
        Coroutines.io(this) {
            favListRoomBaseRepository.delete(raduiuid)
            tasksEventChannel.send(RoomListEvents.ProdDeleteFromRoomMsg(msg))
        }
    }







    fun deleteAll(msg: String) {
        Coroutines.io(this) {
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
            favListRoomBaseRepository.delete(raduiuid)
            tasksEventChannel.send(RoomListEvents.ProdDeleteFromRoomMsg(msg))
        }
    }

    fun deleteAllAlarm(msg: String) {
        Coroutines.io(this) {
            favListRoomBaseRepository.deleteAllAlarm()
            tasksEventChannel.send(RoomListEvents.ProdDeleteFromRoomMsg(msg))
        }
    }

    private fun getAllAlarm() {

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





    sealed class RoomListEvents {
        data class ProdAddToRoomMsg(val msg: String) : RoomListEvents()
        data class ProdDeleteFromRoomMsg(val msg: String) : RoomListEvents()
    }
}
