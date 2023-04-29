package com.amirami.simapp.radiostations.pairalarm.viewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.amirami.simapp.radiostations.pairalarm.database.table.AlarmData
import com.amirami.simapp.radiostations.pairalarm.model.CalculatorProblem
import com.amirami.simapp.radiostations.pairalarm.repository.AlarmRepository
import com.amirami.simapp.radiostations.pairalarm.util.cancelAlarm
import com.amirami.simapp.radiostations.pairalarm.util.initCurrentAlarmData
import com.amirami.simapp.radiostations.pairalarm.util.setAlarmOnBroadcast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val app: Application
) : BaseViewModel(app) {
    val currentAlarmHour = MutableStateFlow(0)
    val currentAlarmMin = MutableStateFlow(0)
    val currentAlarmBell = MutableStateFlow(0)
    val currentAlarmMode = MutableStateFlow(0)
    val answer = MutableStateFlow("")

    fun getAllAlarmData() = alarmRepository.getAllAlarm()

    fun insertAlarmData(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.insertAlarmData(alarmData)
    }.execute(onSuccess = {
        // 브로드캐스트에 알람 예약하기
        setAlarmOnBroadcast(
            app.applicationContext,
            alarmData.alarmCode.toInt(),
            alarmData.hour,
            alarmData.minute
        )
    }
    )

    fun updateAlarmData(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.updateAlarmData(alarmData)
    }.execute(
        onSuccess = {
            // 브로드캐스트에 기존 알람 삭제 및 새로운 알람 추가
            if (alarmData.alarmIsOn) {
                cancelAlarm(app.applicationContext, alarmData.alarmCode)
                setAlarmOnBroadcast(
                    app.applicationContext,
                    alarmData.alarmCode.toInt(),
                    alarmData.hour,
                    alarmData.minute
                )
            } else {
                cancelAlarm(app.applicationContext, alarmData.alarmCode)
            }
        }
    )

    fun deleteAlarmData(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.deleteAlarmData(alarmData)
    }.execute(onSuccess = {
        // 브로드캐스트에 알람 삭제
        cancelAlarm(app.applicationContext, alarmData.alarmCode)
    })

    fun searchAlarmCode(alarmCode: String) = alarmRepository.searchWithAlarmCode(alarmCode)

    fun getAlarmData(alarmCode: String): Flow<AlarmData> {
        return if (alarmCode != "Empty") {
            alarmRepository.searchWithAlarmCode(alarmCode)
        } else {
            initCurrentAlarmDataFlow()
        }
    }

    private fun initCurrentAlarmDataFlow(): Flow<AlarmData> {
        return flowOf(
            initCurrentAlarmData()
        )
    }

    fun getRandomNumberForCalculator(): CalculatorProblem {
        val random = Random()
        val randomNumber1 = random.nextInt(100)
        val randomNumber2 = random.nextInt(100)
        return CalculatorProblem(
            randomNumber1,
            randomNumber2,
            (randomNumber1 + randomNumber2).toString()
        )
    }
}
