package com.amirami.simapp.radiobroadcast.pairalarm.model

enum class SettingContents(val title: String) {
    QUICKALARM_MODE("QuickAlarm Mode"),
    QUICKALARM_MUTE("QuickAlarm Mute"),
    BLANK("")
}

// AlarmBell
typealias BellName = String
typealias BellIndex = Int

enum class BellType(val title: BellName) {
    WALKING("Walking"),
    PINAOMAN("PianoMan"),
    HAPPYTOWN("HappyTown"),
    LONELY("Lonely")
}

fun BellName.getBellIndex(): Int {
    return when (this) {
        BellType.WALKING.title -> 0
        BellType.PINAOMAN.title -> 1
        BellType.HAPPYTOWN.title -> 2
        BellType.LONELY.title -> 3
        else -> 0
    }
}

fun BellIndex.getBellName(): String {
    return when (this) {
        0 -> BellType.WALKING.title
        1 -> BellType.PINAOMAN.title
        2 -> BellType.HAPPYTOWN.title
        3 -> BellType.LONELY.title
        else -> BellType.WALKING.title
    }
}


// AlarmMode
typealias AlarmModeName = String

enum class AlarmMode(val mode: AlarmModeName) {
    NORMAL("Normal"),
    CALCULATION("Calculation")
}

fun AlarmModeName.getModeIndex(): Int {
    return when (this) {
        AlarmMode.NORMAL.mode -> 0
        AlarmMode.CALCULATION.mode -> 1
        else -> 0
    }
}

// AlarmMute
typealias AlarmVibrationName = String
enum class AlarmVibrationOption(val vibrationOptionName: AlarmVibrationName) {
    Vibration("Mute"),
    ONCE("Once"),
    SOUND("Sound")
}

fun AlarmVibrationName.getVibrationIndex(): Int {
    return when (this) {
        AlarmVibrationOption.Vibration.vibrationOptionName -> 0
        AlarmVibrationOption.ONCE.vibrationOptionName -> 1
        AlarmVibrationOption.SOUND.vibrationOptionName -> 2
        else -> 0
    }
}