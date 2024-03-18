package com.amirami.simapp.radiobroadcastpro.pairalarm.repository

import com.amirami.simapp.radiobroadcastpro.pairalarm.database.table.AlarmData
import kotlinx.coroutines.flow.Flow

interface AlarmInterface {

    fun getAllAlarm(): Flow<List<AlarmData>>

    suspend fun insertAlarmData(alarmData: AlarmData)

    suspend fun updateAlarmData(alarmData: AlarmData)

    suspend fun deleteAlarmData(alarmData: AlarmData)

    fun searchWithAlarmCode(alarmCode: String): Flow<AlarmData>
}