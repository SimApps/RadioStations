package com.amirami.simapp.radiostations.viewmodel

import android.app.Application
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import com.amirami.simapp.radiostations.Exoplayer
import com.amirami.simapp.radiostations.MainActivity
import com.amirami.simapp.radiostations.RadioFunction
import com.amirami.simapp.radiostations.RadioFunction.stopService
import com.amirami.simapp.radiostations.hiltcontainer.RadioApplication
import com.amirami.simapp.radiostations.model.RadioVariables
import com.amirami.simapp.radiostations.utils.datamonitor.DataUsageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    app: Application
) : AndroidViewModel(app) { // : ViewModel() {

    private val dialogueEventsChannel = MutableSharedFlow<ChooseDefBottomSheetEvents>()
    val dialogueEvents = dialogueEventsChannel.asSharedFlow()

    private val recordEventsChannel = MutableSharedFlow<RecordInfoEvents>()
    val recordEvents = recordEventsChannel.asSharedFlow()

    private val _putRadioInfo = MutableStateFlow(RadioVariables())
    val putRadioInfo = _putRadioInfo.asStateFlow()

    private val _putRadioPlayerInfo = MutableStateFlow(RadioVariables())
    val putRadioPlayerInfo: StateFlow<RadioVariables>
        get() = _putRadioPlayerInfo

    private val _putRadioAlarmInfo = MutableStateFlow(RadioVariables())
    val putRadioAlarmInfo: StateFlow<RadioVariables>
        get() = _putRadioAlarmInfo

    private val _putTitleText = MutableStateFlow("")
    val putTitleText: StateFlow<String>
        get() = _putTitleText

    private val _putTheme = MutableStateFlow(true)
    val putTheme = _putTheme.asStateFlow()

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkStatsManager = getApplication<RadioApplication>().getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
                val managers = getApplication<RadioApplication>().packageManager
                val info: ApplicationInfo = managers.getApplicationInfo(Exoplayer.packagename, 0)
                val uid: Int = info.uid
                // val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                // val manager= DataUsageManager(networkStatsManager, Settings.Secure.ANDROID_ID)
                // val manager= DataUsageManager(networkStatsManager, uid.toString())
                val manager = DataUsageManager(networkStatsManager, Exoplayer.packagename)

                if (MainActivity.data == 0L) {
                    MainActivity.data = manager.getUsage(uid).downloads + manager.getUsage(uid).uploads
                }

                while (true) {
                    _putDataConsumption.emit(RadioFunction.bytesIntoHumanReadable(manager.getUsage(uid).downloads + manager.getUsage(uid).uploads - MainActivity.data))
                    delay(10)
                }
            } else _putDataConsumption.emit("Not Suported")
        }
    }

    fun putDataConsumptiontimer(data: kotlin.Long) {
        job = viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkStatsManager = getApplication<RadioApplication>().getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
                val managers = getApplication<RadioApplication>().packageManager
                val info: ApplicationInfo = managers.getApplicationInfo(Exoplayer.packagename, 0)
                val uid: Int = info.uid
                // val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                // val manager= DataUsageManager(networkStatsManager, Settings.Secure.ANDROID_ID)
                // val manager= DataUsageManager(networkStatsManager, uid.toString())
                val manager = DataUsageManager(networkStatsManager, Exoplayer.packagename)

                if (MainActivity.data == 0L) {
                    MainActivity.data = manager.getUsage(uid).downloads + manager.getUsage(uid).uploads
                }

                MainActivity.initial_data_consumed = manager.getUsage(uid).downloads + manager.getUsage(uid).uploads - MainActivity.data

                while (true) {
                    val final_data_toconsume = MainActivity.initial_data_consumed + data

                    _putDataConsumptionTimer.value = final_data_toconsume - ((manager.getUsage(uid).downloads + manager.getUsage(uid).uploads - MainActivity.data))
                    delay(10)
                }
            } else _putDataConsumptionTimer.value = -1251L // "Not Suported"
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
        if (Exoplayer.is_downloading) {
            MainActivity.downloader?.cancelDownload()
            Exoplayer.is_downloading = false // without this line player continue paly and rec stop
        }
        stopService(getApplication<RadioApplication>(), true)
        //   Exoplayer.releasePlayer(getApplication<RadioApplication>())
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

    fun putPutDefServerInfo(server: String) {
        viewModelScope.launch {
            dialogueEventsChannel.emit(ChooseDefBottomSheetEvents.PutDefServerInfo(server))
        }
    }

    fun putPutDefThemeInfo(dark: Boolean, systheme: Boolean) {
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

    fun putRadioInfo(radiovar: RadioVariables) {
        viewModelScope.launch {
            _putRadioInfo.value = radiovar
        }
    }

    fun putRadiopalyerInfo(radiovar: RadioVariables) {
        viewModelScope.launch {
            // radioEventsChannel.send(RadioInfoEvents.PutDefCountryInfo(radiovar))

            // _putRadioPlayerInfo.value = radiovar
            _putRadioPlayerInfo.emit(radiovar)
        }
    }

    fun putRadioalarmInfo(radiovar: RadioVariables) {
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

    fun putThemes(theme: Boolean) {
        viewModelScope.launch {
            _putTheme.value = theme

            //   _putTheme.emit(theme)
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
}
