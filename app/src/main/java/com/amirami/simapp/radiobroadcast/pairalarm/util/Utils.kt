package com.amirami.simapp.radiobroadcast.pairalarm.util

import com.amirami.simapp.radiobroadcast.model.RadioEntity
import com.amirami.simapp.radiobroadcast.pairalarm.database.table.AlarmData
import java.util.*

fun getNewAlarmCode(): String {
    val calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMin = calendar.get(Calendar.MINUTE)
    val currentSecond = calendar.get(Calendar.SECOND)

    return currentDay.toString() + currentHour.toString() +
            currentMin.toString() + currentSecond.toString()
}




// SimpleAlarm 이나 QuickAlarm 으로 만든 알람의 calendar 값을 얻는다
fun getAddedTime(hour: Int, min: Int): Calendar {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.HOUR, hour)
        add(Calendar.MINUTE, min)
        timeInMillis
    }
    return calendar
}

// AlarmData를 만드는데 필요한 데이터를 매개변수로 받고 AlarmData를 반환 - SimpleAlarm과 QuickAlarm에서 사용된다
fun makeAlarmData(
    calendar: Calendar,
    alarmName: String,
    alarmData: AlarmData,
    radio :RadioEntity
): AlarmData {
    val setWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val vibration = alarmData.vibration
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val min = calendar.get(Calendar.MINUTE)
    val volume = alarmData.volume
    val bell = alarmData.bell
    val mode = alarmData.mode
    val alarmCode = getNewAlarmCode()
    val radio = radio

    // 모든 요일이 false인 상태
    val defaultAlarmData = AlarmData(
        id = null,
        alarmIsOn = true,
        Sun = false,
        Mon = false,
        Tue = false,
        Wed = false,
        Thu = false,
        Fri = false,
        Sat = false,
        vibration = vibration,
        alarmCode = alarmCode,
        mode = mode,
        hour = hour,
        minute = min,
        quick = true,
        volume = volume,
        bell = bell,
        name = alarmName,
        radio = radio
    )

    // 일요일=1 ~ 토요일 = 7임
    return when (setWeek) {
        // 일
        1 -> defaultAlarmData.copy(Sun = true)
        2 -> defaultAlarmData.copy(Mon = true)
        3 -> defaultAlarmData.copy(Tue = true)
        4 -> defaultAlarmData.copy(Wed = true)
        5 -> defaultAlarmData.copy(Thu = true)
        6 -> defaultAlarmData.copy(Fri = true)
        else -> defaultAlarmData.copy(Sat = true)
    }
}

