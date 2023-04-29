package com.amirami.simapp.radiostations.pairalarm.repository

import com.amirami.simapp.radiostations.pairalarm.database.dao.AlarmDAO
import com.amirami.simapp.radiostations.pairalarm.database.table.AlarmData
import com.amirami.simapp.radiostations.pairalarm.repository.AlarmInterface

class AlarmRepository(private val dao: AlarmDAO): AlarmInterface {

    override fun getAllAlarm() = dao.getAllAlarms()

    override suspend fun insertAlarmData(alarmData: AlarmData) {
        dao.insertNewAlarm(alarmData)
    }

    override suspend fun updateAlarmData(alarmData: AlarmData) {
        dao.updateAlarm(alarmData)
    }

    override suspend fun deleteAlarmData(alarmData: AlarmData) {
        dao.deleteAlarm(alarmData)
    }

    override fun searchWithAlarmCode(alarmCode: String) =
        dao.searchAlarmDataWithAlarmCode(alarmCode)
}
