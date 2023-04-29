package com.amirami.simapp.radiostations.pairalarm.util

import android.annotation.SuppressLint
import com.amirami.simapp.radiostations.pairalarm.database.table.AlarmData
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun transMillisToTime(milliSecond: Long): String? {
    val simpleDateFormat = SimpleDateFormat("MM/dd  HH:mm")
    return simpleDateFormat.format(milliSecond)
}

fun getCurrentHour(): Int {
    return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
}

fun getCurrentMinute(): Int {
    return Calendar.getInstance().get(Calendar.MINUTE)
}

fun getCurrentMinuteDoubleDigitWithString(): String {
    val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)
    return if (currentMinute < 10) {
        "0$currentMinute"
    } else {
        currentMinute.toString()
    }
}

fun getCurrentHourDoubleDigitWithString(): String {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return if (currentHour < 10) {
        "0$currentHour"
    } else {
        currentHour.toString()
    }
}

fun getMillisWithCalendar(hour: Int, min: Int, dayOfWeek: Int): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, min)
        set(Calendar.SECOND, 0)
        set(Calendar.DAY_OF_WEEK, dayOfWeek)
    }
    val alarmMillis = calendar.timeInMillis

    if (alarmMillis <= Calendar.getInstance().timeInMillis) {
        calendar.add(Calendar.DAY_OF_WEEK, 7)
    }

    return calendar.timeInMillis
}

fun getAlarmDataFromTimeMillis(
    timeInterval: Long,
    quickAlarmBellIndex: Int = 0,
    quickAlarmModeIndex: Int = 0,
    quickAlarmVibrationIndex: Int = 0
): AlarmData {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis += timeInterval
    val hourSet = calendar.get(Calendar.HOUR_OF_DAY)
    val minSet = calendar.get(Calendar.MINUTE)
    var alarmSet = initCurrentAlarmData(
        hour = hourSet,
        min = minSet,
        isQuick = true,
        alarmCode = getNewAlarmCode(),
        bell = quickAlarmBellIndex,
        mode = quickAlarmModeIndex,
        vibration = quickAlarmVibrationIndex
    )
  //  Timber.d("hourSet: $hourSet")
  //  Timber.d("minSet: $minSet")
    // 요일 데이터는 Int형으로 가져온다 (일요일:1 ~ 토요일:7)
    when (calendar.get(Calendar.DAY_OF_WEEK)) {
        1 -> alarmSet = alarmSet.copy(Sun = true)
        2 -> alarmSet = alarmSet.copy(Mon = true)
        3 -> alarmSet = alarmSet.copy(Tue = true)
        4 -> alarmSet = alarmSet.copy(Wed = true)
        5 -> alarmSet = alarmSet.copy(Thu = true)
        6 -> alarmSet = alarmSet.copy(Fri = true)
        7 -> alarmSet = alarmSet.copy(Sat = true)
    }

    return alarmSet
}
